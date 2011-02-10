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
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Message
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
name|security
operator|.
name|SimpleSecurityBrokerSystemTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicSendReceiveWithEmbeddedBrokerAndUserIDTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsAndEmbeddedBrokerTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JmsTopicSendReceiveWithEmbeddedBrokerAndUserIDTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|userName
init|=
literal|"James"
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|answer
init|=
name|super
operator|.
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
return|return
name|answer
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
name|setPopulateJMSXUserID
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMessagesReceivedAreValid
parameter_list|(
name|List
name|receivedMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
operator|.
name|assertMessagesReceivedAreValid
argument_list|(
name|receivedMessages
argument_list|)
expr_stmt|;
comment|// lets assert that the user ID is set
for|for
control|(
name|Iterator
name|iter
init|=
name|receivedMessages
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"JMSXUserID"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message with userID: "
operator|+
name|userID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JMSXUserID header"
argument_list|,
name|userName
argument_list|,
name|userID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

