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
name|ra
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAException
import|;
end_import

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
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|UnsubscribeResubscribeTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"vm://localhost"
decl_stmt|;
specifier|private
name|ConnectionManagerAdapter
name|connectionManager
init|=
operator|new
name|ConnectionManagerAdapter
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|managedConnectionFactory
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|ManagedConnectionProxy
name|connection
decl_stmt|;
specifier|private
name|ActiveMQManagedConnection
name|managedConnection
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|managedConnectionFactory
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setServerUrl
argument_list|(
name|DEFAULT_HOST
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setUserName
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setClientid
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|getConnection
parameter_list|()
throws|throws
name|ResourceException
throws|,
name|JMSException
block|{
name|connectionFactory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
name|connectionManager
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|ManagedConnectionProxy
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|managedConnection
operator|=
name|connection
operator|.
name|getManagedConnection
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testUnsubscribeResubscribe
parameter_list|()
throws|throws
name|ResourceException
throws|,
name|JMSException
throws|,
name|XAException
block|{
name|getConnection
argument_list|()
expr_stmt|;
name|Session
name|session
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
name|session
operator|.
name|createTopic
argument_list|(
literal|"topic"
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub"
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"text message"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|sub
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
literal|"sub"
argument_list|)
expr_stmt|;
name|sub
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"sub"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

