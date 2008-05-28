begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_comment
comment|/**  * Tests for the BrokerService class  *   * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|BrokerServiceTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testAddRemoveTransportsWithJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|removeConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRemoveTransportsWithoutJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|removeConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRemoveNetworkWithJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NetworkConnector
name|connector
init|=
name|service
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://group-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|removeNetworkConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testAddRemoveNetworkWithoutJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|NetworkConnector
name|connector
init|=
name|service
operator|.
name|addNetworkConnector
argument_list|(
literal|"multicast://group-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|service
operator|.
name|removeNetworkConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

