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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
operator|.
name|LevelDBStoreFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|SimpleDurableTopicTest
extends|extends
name|SimpleTopicTest
block|{
specifier|protected
name|long
name|initialConsumerDelay
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|numberOfDestinations
operator|=
literal|1
expr_stmt|;
name|numberOfConsumers
operator|=
literal|1
expr_stmt|;
name|numberofProducers
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SimpleDurableTopicTest.numberofProducers"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|sampleCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"SimpleDurableTopicTest.sampleCount"
argument_list|,
literal|"1000"
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|playloadSize
operator|=
literal|1024
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|LevelDBStoreFactory
name|persistenceFactory
init|=
operator|new
name|LevelDBStoreFactory
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setPersistenceFactory
argument_list|(
name|persistenceFactory
argument_list|)
expr_stmt|;
comment|//answer.setDeleteAllMessagesOnStartup(true);
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
block|}
annotation|@
name|Override
specifier|protected
name|PerfProducer
name|createProducer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|number
parameter_list|,
name|byte
name|payload
index|[]
parameter_list|)
throws|throws
name|JMSException
block|{
name|PerfProducer
name|pp
init|=
operator|new
name|PerfProducer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
name|payload
argument_list|)
decl_stmt|;
name|pp
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
return|return
name|pp
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PerfConsumer
name|createConsumer
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|int
name|number
parameter_list|)
throws|throws
name|JMSException
block|{
name|PerfConsumer
name|result
init|=
operator|new
name|PerfConsumer
argument_list|(
name|fac
argument_list|,
name|dest
argument_list|,
literal|"subs:"
operator|+
name|number
argument_list|)
decl_stmt|;
name|result
operator|.
name|setInitialDelay
argument_list|(
name|this
operator|.
name|initialConsumerDelay
argument_list|)
expr_stmt|;
return|return
name|result
return|;
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
name|super
operator|.
name|createConnectionFactory
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|//result.setSendAcksAsync(false);
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

