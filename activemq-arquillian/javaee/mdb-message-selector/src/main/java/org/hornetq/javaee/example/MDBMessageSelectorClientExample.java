begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2009 Red Hat, Inc.  *  Red Hat licenses this file to you under the Apache License, version  *  2.0 (the "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  *  implied.  See the License for the specific language governing  *  permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
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
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>  */
end_comment

begin_class
specifier|public
class|class
name|MDBMessageSelectorClientExample
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
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|InitialContext
name|initialContext
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//Step 1. Create an initial context to perform the JNDI lookup.
specifier|final
name|Properties
name|env
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"org.jboss.naming.remote.client.InitialContextFactory"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|"remote://localhost:4447"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|initialContext
operator|=
operator|new
name|InitialContext
argument_list|(
name|env
argument_list|)
expr_stmt|;
comment|//Step 2. Perfom a lookup on the queue
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|initialContext
operator|.
name|lookup
argument_list|(
literal|"jms/queues/testQueue"
argument_list|)
decl_stmt|;
comment|//Step 3. Perform a lookup on the Connection Factory
name|ConnectionFactory
name|cf
init|=
operator|(
name|ConnectionFactory
operator|)
name|initialContext
operator|.
name|lookup
argument_list|(
literal|"jms/RemoteConnectionFactory"
argument_list|)
decl_stmt|;
comment|//Step 4.Create a JMS Connection
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"guest"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
comment|//Step 5. Create a JMS Session
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
comment|//Step 6. Create a JMS Message Producer
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
comment|//Step 7. Create a Text Message and set the color property to blue
name|TextMessage
name|blueMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"This is a text message"
argument_list|)
decl_stmt|;
name|blueMessage
operator|.
name|setStringProperty
argument_list|(
literal|"color"
argument_list|,
literal|"BLUE"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent message: "
operator|+
name|blueMessage
operator|.
name|getText
argument_list|()
operator|+
literal|" color=BLUE"
argument_list|)
expr_stmt|;
comment|//Step 8. Send the Message
name|producer
operator|.
name|send
argument_list|(
name|blueMessage
argument_list|)
expr_stmt|;
comment|//Step 9. create another message and set the color property to red
name|TextMessage
name|redMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"This is a text message"
argument_list|)
decl_stmt|;
name|redMessage
operator|.
name|setStringProperty
argument_list|(
literal|"color"
argument_list|,
literal|"RED"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sent message: "
operator|+
name|redMessage
operator|.
name|getText
argument_list|()
operator|+
literal|" color=RED"
argument_list|)
expr_stmt|;
comment|//Step 10. Send the Message
name|producer
operator|.
name|send
argument_list|(
name|redMessage
argument_list|)
expr_stmt|;
comment|//Step 10,11 and 12 in MDBMessageSelectorExample
block|}
finally|finally
block|{
comment|//Step 13. Be sure to close our JMS resources!
if|if
condition|(
name|initialContext
operator|!=
literal|null
condition|)
block|{
name|initialContext
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

