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
package org.apache.ibatis.builder;

import org.apache.ibatis.cache.Cache;

/**
 * 只有一个业务方法调用助手进行解析。
 * 为什么不直接调用助手进行解析呢？
 * 因为会出现解析未完成的情况，需要进行记录。如果封装成1个实体，则保存比较好处理
 * @author Clinton Begin
 */
public class CacheRefResolver {
  private final MapperBuilderAssistant assistant;//当前mapper.xml文件的解析助手
  private final String cacheRefNamespace;     //cache-ref节点指向的namespace

  public CacheRefResolver(MapperBuilderAssistant assistant, String cacheRefNamespace) {
    this.assistant = assistant;
    this.cacheRefNamespace = cacheRefNamespace;
  }

  public Cache resolveCacheRef() {
    return assistant.useCacheRef(cacheRefNamespace);
  }
}