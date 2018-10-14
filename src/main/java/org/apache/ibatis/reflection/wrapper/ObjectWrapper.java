/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 某个类的所有信息包装
 *    1、类中所有的get方法
 *    2、...所有的set方法
 *    3、...获取某个set方法的参数类型
 *    4、...获取某个get方法的返回类型
 *    5、...判断是否有对应的set/get方法
 *    6、是否是集合
 *    7、其他具体看实现类
 * @author Clinton Begin
 */
public interface ObjectWrapper {

  /**
   * 从${@ObjectWrapper}对象中的object属性中，获取Prop表达式对应的值
   * 这里需要考虑到集合的表达式获取比较特殊，需要递归到最底层的一个name
   * @param prop
   * @return
   */
  Object get(PropertyTokenizer prop);

  void set(PropertyTokenizer prop, Object value);

  String findProperty(String name, boolean useCamelCaseMapping);

  String[] getGetterNames();

  String[] getSetterNames();

  Class<?> getSetterType(String name);

  Class<?> getGetterType(String name);

  boolean hasSetter(String name);

  boolean hasGetter(String name);

  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);
  
  boolean isCollection();

  //集合才会用到该接口
  void add(Object element);

  //集合才会用到该接口
  <E> void addAll(List<E> element);

}
