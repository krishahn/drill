/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.sys;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.TestWithZookeeper;
import org.apache.drill.exec.store.sys.local.LocalPStoreProvider;
import org.apache.drill.exec.store.sys.zk.ZkPStoreProvider;
import org.junit.Test;

public class TestPStoreProviders extends TestWithZookeeper {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestPStoreProviders.class);

  static LocalPStoreProvider provider;

  @Test
  public void verifyLocalStore() throws Exception {
    try(LocalPStoreProvider provider = new LocalPStoreProvider(DrillConfig.create())){
      PStoreTestUtil.test(provider);
    }
  }

  @Test
  public void verifyZkStore() throws Exception {
    DrillConfig config = getConfig();
    String connect = config.getString(ExecConstants.ZK_CONNECTION);
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
    .namespace(config.getString(ExecConstants.ZK_ROOT))
    .retryPolicy(new RetryNTimes(1, 100))
    .connectionTimeoutMs(config.getInt(ExecConstants.ZK_TIMEOUT))
    .connectString(connect);

    try(CuratorFramework curator = builder.build()){
      curator.start();
      ZkPStoreProvider provider = new ZkPStoreProvider(config, curator);
      PStoreTestUtil.test(provider);
    }
  }
}
