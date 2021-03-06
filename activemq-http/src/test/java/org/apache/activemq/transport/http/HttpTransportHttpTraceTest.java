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
name|transport
operator|.
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|HttpTransportHttpTraceTest
block|{
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|uri
decl_stmt|;
specifier|protected
name|String
name|enableTraceParam
decl_stmt|;
specifier|private
name|int
name|expectedStatus
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|additionalConfig
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|getConnectorUri
argument_list|()
argument_list|)
decl_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|uri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|String
name|getConnectorUri
parameter_list|()
block|{
return|return
literal|"http://localhost:0?"
operator|+
name|enableTraceParam
return|;
block|}
specifier|protected
name|void
name|additionalConfig
parameter_list|()
block|{      }
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|HttpTraceTestSupport
operator|.
name|getTestParameters
argument_list|()
return|;
block|}
specifier|public
name|HttpTransportHttpTraceTest
parameter_list|(
specifier|final
name|String
name|enableTraceParam
parameter_list|,
specifier|final
name|int
name|expectedStatus
parameter_list|)
block|{
name|this
operator|.
name|enableTraceParam
operator|=
name|enableTraceParam
expr_stmt|;
name|this
operator|.
name|expectedStatus
operator|=
name|expectedStatus
expr_stmt|;
block|}
comment|/**      * This tests whether the TRACE method is enabled or not      * @throws Exception      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testHttpTraceEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpTraceTestSupport
operator|.
name|testHttpTraceEnabled
argument_list|(
name|uri
argument_list|,
name|expectedStatus
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

