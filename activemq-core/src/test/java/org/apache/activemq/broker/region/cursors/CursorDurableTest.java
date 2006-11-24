begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
operator|.
name|region
operator|.
name|cursors
package|;
end_package

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
name|ConnectionFactory
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|region
operator|.
name|policy
operator|.
name|StorePendingDurableSubscriberMessageStoragePolicy
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|CursorDurableTest
extends|extends
name|CursorSupport
block|{
specifier|protected
name|Destination
name|getDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|topicName
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|getConsumerConnection
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|fac
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"testConsumer"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|MessageConsumer
name|getConsumer
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|consumerSession
init|=
name|connection
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
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|getDestination
argument_list|(
name|consumerSession
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"testConsumer"
argument_list|)
decl_stmt|;
return|return
name|consumer
return|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
block|{
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPendingDurableSubscriberPolicy
argument_list|(
operator|new
name|StorePendingDurableSubscriberMessageStoragePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

