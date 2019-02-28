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
package org.apache.ibatis.cache.decorators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

/**
 * Lru (least recently used) cache decorator
 *  最近没有被使用的key优先被清理
 * @author Clinton Begin
 */
public class LruCache implements Cache {

  private final Cache delegate;
  private Map<Object, Object> keyMap;
  private Object eldestKey;

  public LruCache(Cache delegate) {
    this.delegate = delegate;
    setSize(1024);
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  public void setSize(final int size) {
    // 为什么要使用LinkedHashMap 带有排序功能的map？
    //    -> 因为其排序map创建时，若指定accessOrder值为true，则在调用排序map的get方法时，会将get到的key放到map的尾部
    //    -> 从而达到经常使用的排在map的后面，不常使用的排在map的前面的效果
    // 初始化keyMap，并重写removeEldestEntry方法，该方法什么时候会被调用呢？
    //    -> 并重写removeEldestEntry方法会在map调用put(key,value)的使用触发，并且在map的first不为Null的时候触发，传进来的是map的first（即第一个）node
    keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
      private static final long serialVersionUID = 4267176411845948333L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        boolean tooBig = size() > size; //如果keyMap缓存的key数量大于规定的size，（map一发生扩容时就会大于size，因为map初始化大小就是size
        if (tooBig) {
          //如果超过规定的size，则需要移除first节点
          eldestKey = eldest.getKey();
        }
        return tooBig;
      }
    };
  }

  @Override
  public void putObject(Object key, Object value) {
    delegate.putObject(key, value);
    //每次放置缓存的时候，都要去检查下有没有最少使用的Key需要移除
    cycleKeyList(key);
  }

  @Override
  public Object getObject(Object key) {
    // 每次调用get方法，都会将key移动到map的最后一个位置，以此表示该key刚被使用过
    // 通过这样的移动，来完成最少使用的排在map的前面
    keyMap.get(key); //touch
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    //可以不用删除keyMap对应的key? -> 迟早会被移除？
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyMap.clear();
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return null;
  }

  private void cycleKeyList(Object key) {
    keyMap.put(key, key);
    if (eldestKey != null) {
      delegate.removeObject(eldestKey);
      eldestKey = null;
    }
  }

}
