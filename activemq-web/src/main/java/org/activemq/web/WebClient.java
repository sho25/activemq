begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Externalizable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|MessageConsumer
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
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionActivationListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnection
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|ActiveMQSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
import|;
end_import

begin_comment
comment|/**  * Represents a messaging client used from inside a web container  * typically stored inside a HttpSession  *  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|WebClient
implements|implements
name|HttpSessionActivationListener
implements|,
name|Externalizable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|webClientAttribute
init|=
literal|"org.activemq.webclient"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|connectionFactoryAttribute
init|=
literal|"org.activemq.connectionFactory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|queueConsumersAttribute
init|=
literal|"org.activemq.queueConsumers"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|brokerUrlInitParam
init|=
literal|"org.activemq.brokerURL"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|WebClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|transient
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|private
specifier|static
specifier|transient
name|Map
name|queueConsumers
decl_stmt|;
specifier|private
specifier|transient
name|ServletContext
name|context
decl_stmt|;
specifier|private
specifier|transient
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
specifier|transient
name|ActiveMQSession
name|session
decl_stmt|;
specifier|private
specifier|transient
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
specifier|transient
name|Map
name|topicConsumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|private
specifier|final
name|Semaphore
name|semaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * @return the web client for the current HTTP session or null if there is not a web client created yet      */
specifier|public
specifier|static
name|WebClient
name|getWebClient
parameter_list|(
name|HttpSession
name|session
parameter_list|)
block|{
return|return
operator|(
name|WebClient
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
name|webClientAttribute
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|initContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
name|factory
operator|=
name|initConnectionFactory
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No ConnectionFactory available in the ServletContext for: "
operator|+
name|connectionFactoryAttribute
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAttribute
argument_list|(
name|connectionFactoryAttribute
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
name|queueConsumers
operator|=
name|initQueueConsumers
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Only called by serialization      */
specifier|public
name|WebClient
parameter_list|()
block|{     }
specifier|public
name|WebClient
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|initContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|deliveryMode
return|;
block|}
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|deliveryMode
parameter_list|)
block|{
name|this
operator|.
name|deliveryMode
operator|=
name|deliveryMode
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Closing the WebClient!!! "
operator|+
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|producer
operator|=
literal|null
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
name|topicConsumers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|topicConsumers
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|producer
operator|==
literal|null
condition|)
block|{
name|producer
operator|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sent! to destination: "
operator|+
name|destination
operator|+
literal|" message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
block|{
name|session
operator|=
name|createSession
argument_list|()
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
specifier|public
name|ActiveMQConnection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
specifier|public
name|void
name|sessionWillPassivate
parameter_list|(
name|HttpSessionEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not close connection: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|sessionDidActivate
parameter_list|(
name|HttpSessionEvent
name|event
parameter_list|)
block|{
comment|// lets update the connection factory from the servlet context
name|context
operator|=
name|event
operator|.
name|getSession
argument_list|()
operator|.
name|getServletContext
argument_list|()
expr_stmt|;
name|initContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Map
name|initQueueConsumers
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
name|Map
name|answer
init|=
operator|(
name|Map
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|queueConsumersAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|context
operator|.
name|setAttribute
argument_list|(
name|queueConsumersAttribute
argument_list|,
name|answer
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
specifier|static
name|ConnectionFactory
name|initConnectionFactory
parameter_list|(
name|ServletContext
name|servletContext
parameter_list|)
block|{
name|ConnectionFactory
name|connectionFactory
init|=
operator|(
name|ConnectionFactory
operator|)
name|servletContext
operator|.
name|getAttribute
argument_list|(
name|connectionFactoryAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
name|String
name|brokerURL
init|=
operator|(
name|String
operator|)
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|brokerUrlInitParam
argument_list|)
decl_stmt|;
name|servletContext
operator|.
name|log
argument_list|(
literal|"Value of: "
operator|+
name|brokerUrlInitParam
operator|+
literal|" is: "
operator|+
name|brokerURL
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerURL
operator|==
literal|null
condition|)
block|{
name|brokerURL
operator|=
literal|"vm://localhost"
expr_stmt|;
block|}
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
name|connectionFactory
operator|=
name|factory
expr_stmt|;
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|connectionFactoryAttribute
argument_list|,
name|connectionFactory
argument_list|)
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
specifier|public
specifier|synchronized
name|MessageConsumer
name|getConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|instanceof
name|Topic
condition|)
block|{
name|MessageConsumer
name|consumer
init|=
operator|(
name|MessageConsumer
operator|)
name|topicConsumers
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
name|consumer
operator|=
name|getSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|topicConsumers
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
return|return
name|consumer
return|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|queueConsumers
init|)
block|{
name|SessionConsumerPair
name|pair
init|=
operator|(
name|SessionConsumerPair
operator|)
name|queueConsumers
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
name|pair
operator|=
name|createSessionConsumerPair
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|queueConsumers
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|pair
argument_list|)
expr_stmt|;
block|}
return|return
name|pair
operator|.
name|consumer
return|;
block|}
block|}
block|}
specifier|protected
name|ActiveMQSession
name|createSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|ActiveMQSession
operator|)
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
return|;
block|}
specifier|protected
name|SessionConsumerPair
name|createSessionConsumerPair
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|SessionConsumerPair
name|answer
init|=
operator|new
name|SessionConsumerPair
argument_list|()
decl_stmt|;
name|answer
operator|.
name|session
operator|=
name|createSession
argument_list|()
expr_stmt|;
name|answer
operator|.
name|consumer
operator|=
name|answer
operator|.
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
specifier|static
class|class
name|SessionConsumerPair
block|{
specifier|public
name|Session
name|session
decl_stmt|;
specifier|public
name|MessageConsumer
name|consumer
decl_stmt|;
block|}
specifier|public
name|Semaphore
name|getSemaphore
parameter_list|()
block|{
return|return
name|semaphore
return|;
block|}
block|}
end_class

end_unit

