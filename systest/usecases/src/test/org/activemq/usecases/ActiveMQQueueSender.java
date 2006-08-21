begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|blah
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQQueueSender
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|msg
init|=
name|args
operator|.
name|length
operator|<
literal|1
condition|?
literal|"This is the default message"
else|:
name|args
index|[
literal|0
index|]
decl_stmt|;
name|Queue
name|queue
init|=
literal|null
decl_stmt|;
name|QueueConnectionFactory
name|queueConnectionFactory
init|=
literal|null
decl_stmt|;
name|QueueConnection
name|queueConnection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|//props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.evermind.server.rmi.RMIInitialContextFactory");
comment|//props.setProperty(Context.PROVIDER_URL, "ormi://10.1.0.99:3202/default");
comment|//props.setProperty(Context.SECURITY_PRINCIPAL, "dan");
comment|//props.setProperty(Context.SECURITY_CREDENTIALS, "abc123");
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"org.activemq.jndi.ActiveMQInitialContextFactory"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|"tcp://hostname:61616"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"queue.BlahQueue"
argument_list|,
literal|"example.BlahQueue"
argument_list|)
expr_stmt|;
name|Context
name|jndiContext
init|=
operator|new
name|InitialContext
argument_list|(
name|props
argument_list|)
decl_stmt|;
comment|//queueConnectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/QueueConnectionFactory");
comment|//queue = (Queue) jndiContext.lookup("jms/demoQueue");
name|queueConnectionFactory
operator|=
operator|(
name|QueueConnectionFactory
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
literal|"QueueConnectionFactory"
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|(
name|Queue
operator|)
name|jndiContext
operator|.
name|lookup
argument_list|(
literal|"BlahQueue"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------ERROR-----------------------------"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|queueConnection
operator|=
name|queueConnectionFactory
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|QueueSession
name|queueSession
init|=
name|queueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|QueueSender
name|queueSender
init|=
name|queueSession
operator|.
name|createSender
argument_list|(
name|queue
argument_list|)
decl_stmt|;
comment|//queueSender.setDeliveryMode(DeliveryMode.PERSISTENT);
comment|//queueSender.setTimeToLive(1000*60*60);
name|TextMessage
name|message
init|=
name|queueSession
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"Blah"
argument_list|,
literal|"Hello!"
argument_list|)
expr_stmt|;
name|queueSender
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Message sent"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SOMETHING WENT WRONG WHILE SENDING"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|queueConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|queueConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{             }
block|}
block|}
block|}
block|}
end_class

end_unit

