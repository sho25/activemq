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
name|perf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBDurableTopicTest
extends|extends
name|SimpleDurableTopicTest
block|{
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this.initialConsumerDelay = 10 * 1000;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|result
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|//result.setDispatchAsync(false);
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
comment|//AMQPersistenceAdapterFactory persistenceFactory = new AMQPersistenceAdapterFactory();
comment|//persistenceFactory.setMaxFileLength(1024*16);
comment|//persistenceFactory.setPersistentIndex(true);
comment|//persistenceFactory.setCleanupInterval(10000);
comment|//answer.setPersistenceFactory(persistenceFactory);
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setUseShutdownHook
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setEnableStatistics
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

