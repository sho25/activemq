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
name|usage
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
name|EmbeddedBrokerTestSupport
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
name|util
operator|.
name|ProducerThread
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|Session
import|;
end_import

begin_class
specifier|public
class|class
name|StoreUsageTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|final
name|int
name|WAIT_TIME_MILLS
init|=
literal|20
operator|*
literal|1000
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|10
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|testJmx
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|sess
operator|.
name|createQueue
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|sess
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for the producer to block
name|Thread
operator|.
name|sleep
argument_list|(
name|WAIT_TIME_MILLS
operator|/
literal|2
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|setStoreLimit
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|WAIT_TIME_MILLS
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|producer
operator|.
name|getSentCount
argument_list|()
operator|==
name|producer
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|WAIT_TIME_MILLS
operator|*
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Producer didn't send all messages"
argument_list|,
name|producer
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|producer
operator|.
name|getSentCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
