/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package jp.naist.heijo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import jp.naist.heijo.message.MethodInfo;

public class StructureDB
{

  // メソッドIDとメソッド情報のMap。現状Listでも良いがデバッグの際に便利なので
  public Map<Integer, MethodInfo> IdDataMap = new HashMap<>();

  // メソッドの完全名（パッケージ名+クラス名+メソッド名）とメソッドIDのMap。サンプリングの際、StackTraceElemet->メソッドIDの変換のために用いる
  public Map<String, Integer> NameIdMap = new HashMap<>();

  // クラス名のSet。サンプリングの際、StackTraceElemet.getClassName()がサンプリング対象であるか否かの判別に用いる
  public Set<String> ClassNameSet = new HashSet<>();

  // 監視対象外パッケージ
  public Set<String> IgnorePackageNameSet = new HashSet<>();

  private int methodIdIterator = 0;
  private Set<String> methodNameSet = new HashSet<>();


  public String target = null;
  private String messageHead = "[AGENT]:";

  public void registMethod(String className, String methodName)
  {
    // オーバーロードされたメソッドは区別しない（スタックトレースからは引数の型が取得できないので）
    if (NameIdMap.containsKey(className + "." + methodName)) return;

    // メソッド情報を登録
    MethodInfo method = new MethodInfo(methodIdIterator++, className, methodName);
    IdDataMap.put(method.MethodID, method);
    NameIdMap.put(method.toString(), method.MethodID);

    ClassNameSet.add(className);
  }

  public void setTarget(String target) {
	this.target = target;
}

public boolean isIgnorePackage(String packageName)
  {
    if (packageName == null || packageName.length() == 0) {
      return IgnorePackageNameSet.contains(ConstValue.DEFAULT_PACKAGE_NAME);
    }

    String[] tokens = packageName.split("\\.");

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < tokens.length; i++) {
      builder.append(tokens[i]);
      if (IgnorePackageNameSet.contains(builder.toString() + ".*")) {
        return true;
      }
      if (i == tokens.length - 1 && IgnorePackageNameSet.contains(builder.toString())) {
        return true;
      }
      builder.append(".");
    }
    return false;
  }

  public void collectFromClassPath() throws IOException
  {
    String[] paths = {this.target};
    String[] arrayOfString1;
    int j = (arrayOfString1 = paths).length;
    for (int i = 0; i < j; i++)
    {
      String path = arrayOfString1[i];
      System.out.println(messageHead + path);
    }
    // classファイル、jarファイルを再帰的に走査
    Set<String> classFiles = new HashSet<String>();
    Set<String> jarFiles = new HashSet<String>();
    for (String path : paths) {
      findClassAndJar(new File(path), classFiles, jarFiles);
    }

    // classファイルのバイトコードを調べる
    for (String classFile : classFiles) {
      FileInputStream fis = new FileInputStream(classFile);
      collectClass(fis);
      fis.close();
    }

    // jarファイル内にあるclassファイルのバイトコードを調べる
    for (String jarFile : jarFiles) {
      JarFile file = new JarFile(jarFile);
      for (JarEntry entry : Collections.list(file.entries())) {
        if (entry.getName().endsWith(".class")) {
          collectClass(file.getInputStream(entry));
        }
      }
      file.close();
    }
  }

  private void findClassAndJar(File path, Set<String> classFileSet, Set<String> jarFileSet) throws IOException
  {
    if (path.isDirectory()) {
      for (File p : path.listFiles()) {
        findClassAndJar(p, classFileSet, jarFileSet);
      }
    } else {
      if (path.getName().endsWith(".class")) {
        classFileSet.add(path.getAbsolutePath());
      } else if (path.getName().endsWith(".jar")) {
        jarFileSet.add(path.getAbsolutePath());
      }
    }
  }

  private void collectClass(InputStream stream) throws IOException, RuntimeException
  {
    CtClass klass = ClassPool.getDefault().makeClass(stream);

    // 除外対象の場合は無視
    if (isIgnorePackage(klass.getPackageName())) return;

    boolean notExist = ClassNameSet.add(klass.getName());

    // クラスが重複している場合は無視（クラスパスの優先度は記述順なので、優先度が低い側が無視されるはず）
    if (!notExist) return;

    // コンストラクタ<init>と<clinit>の有無を確認して、存在する場合は追加
    boolean isDecInit = 0 < klass.getDeclaredConstructors().length;
    boolean isDecClinit = false;
    CtConstructor clinit = klass.getClassInitializer();
    isDecClinit = clinit != null && clinit.getDeclaringClass().getName().equals(klass.getName());
    if (isDecInit) {
      MethodInfo data = new MethodInfo(methodIdIterator++, klass.getName(), "<init>");
      IdDataMap.put(data.MethodID, data);
      NameIdMap.put(data.toString(), data.MethodID);
    }
    if (isDecClinit) {
      MethodInfo data = new MethodInfo(methodIdIterator++, klass.getName(), "<clinit>");
      IdDataMap.put(data.MethodID, data);
      NameIdMap.put(data.toString(), data.MethodID);
    }

    for (CtMethod method : klass.getDeclaredMethods()) {
      // メソッド名の重複を防ぐ
      if (!methodNameSet.contains(method.getName())) {
        methodNameSet.add(method.getName());
        MethodInfo data = new MethodInfo(methodIdIterator++, klass.getName(), method.getName());
        IdDataMap.put(data.MethodID, data);
        NameIdMap.put(data.toString(), data.MethodID);
      }
    }
    methodNameSet.clear();
  }

}
