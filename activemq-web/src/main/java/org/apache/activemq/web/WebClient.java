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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
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
name|HttpServletRequest
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
name|HttpSessionBindingEvent
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
name|HttpSessionBindingListener
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
name|MessageAvailableConsumer
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
name|camel
operator|.
name|component
operator|.
name|ActiveMQComponent
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
name|camel
operator|.
name|component
operator|.
name|ActiveMQConfiguration
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
name|pool
operator|.
name|PooledConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|ProducerTemplate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|impl
operator|.
name|DefaultCamelContext
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
comment|/**  * Represents a messaging client used from inside a web container typically  * stored inside a HttpSession TODO controls to prevent DOS attacks with users  * requesting many consumers TODO configure consumers with small prefetch.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|WebClient
implements|implements
name|HttpSessionActivationListener
implements|,
name|HttpSessionBindingListener
implements|,
name|Externalizable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|WEB_CLIENT_ATTRIBUTE
init|=
literal|"org.apache.activemq.webclient"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_FACTORY_ATTRIBUTE
init|=
literal|"org.apache.activemq.connectionFactory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_FACTORY_PREFETCH_PARAM
init|=
literal|"org.apache.activemq.connectionFactory.prefetch"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM
init|=
literal|"org.apache.activemq.connectionFactory.optimizeAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BROKER_URL_INIT_PARAM
init|=
literal|"org.apache.activemq.brokerURL"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SELECTOR_NAME
init|=
literal|"org.apache.activemq.selectorName"
decl_stmt|;
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
specifier|transient
name|Map
argument_list|<
name|Destination
argument_list|,
name|MessageConsumer
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<
name|Destination
argument_list|,
name|MessageConsumer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|transient
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|transient
name|Session
name|session
decl_stmt|;
specifier|private
specifier|transient
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
specifier|public
specifier|static
name|String
name|selectorName
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
specifier|private
name|CamelContext
name|camelContext
decl_stmt|;
specifier|private
name|ProducerTemplate
name|producerTemplate
decl_stmt|;
specifier|public
name|WebClient
parameter_list|()
block|{
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"initContext(ServletContext) not called"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Helper method to get the client for the current session, lazily creating      * a client if there is none currently      *       * @param request is the current HTTP request      * @return the current client or a newly creates      */
specifier|public
specifier|static
name|WebClient
name|getWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|HttpSession
name|session
init|=
name|request
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|WebClient
name|client
init|=
name|getWebClient
argument_list|(
name|session
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
operator|||
name|client
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|client
operator|=
name|WebClient
operator|.
name|createWebClient
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
name|WEB_CLIENT_ATTRIBUTE
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
comment|/**      * @return the web client for the current HTTP session or null if there is      *         not a web client created yet      */
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
name|WEB_CLIENT_ATTRIBUTE
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
name|initConnectionFactory
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAttribute
argument_list|(
literal|"webClients"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|WebClient
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectorName
operator|==
literal|null
condition|)
block|{
name|selectorName
operator|=
name|context
operator|.
name|getInitParameter
argument_list|(
name|SELECTOR_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|selectorName
operator|==
literal|null
condition|)
block|{
name|selectorName
operator|=
literal|"selector"
expr_stmt|;
block|}
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
specifier|synchronized
name|void
name|closeConsumers
parameter_list|()
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|it
init|=
name|consumers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageConsumer
name|consumer
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|setMessageListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|instanceof
name|MessageAvailableConsumer
condition|)
block|{
operator|(
operator|(
name|MessageAvailableConsumer
operator|)
name|consumer
operator|)
operator|.
name|setAvailableListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"caught exception closing consumer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|consumers
operator|!=
literal|null
condition|)
block|{
name|closeConsumers
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
if|if
condition|(
name|producerTemplate
operator|!=
literal|null
condition|)
block|{
name|producerTemplate
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"caught exception closing consumer"
argument_list|,
name|e
argument_list|)
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
name|producerTemplate
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|consumers
operator|!=
literal|null
condition|)
block|{
name|consumers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|consumers
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|consumers
operator|==
literal|null
return|;
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
block|{
if|if
condition|(
name|consumers
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|consumers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Destination
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeObject
argument_list|(
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|write
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
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
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>=
literal|0
condition|)
block|{
name|consumers
operator|=
operator|new
name|HashMap
argument_list|<
name|Destination
argument_list|,
name|MessageConsumer
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|destinationName
init|=
name|in
operator|.
name|readObject
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|Destination
name|destination
init|=
name|destinationName
operator|.
name|startsWith
argument_list|(
literal|"topic://"
argument_list|)
condition|?
operator|(
name|Destination
operator|)
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
else|:
operator|(
name|Destination
operator|)
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|getConsumer
argument_list|(
name|destination
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caought Exception "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|IOException
name|ex
init|=
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|?
name|e
operator|.
name|getCause
argument_list|()
else|:
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
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
name|getProducer
argument_list|()
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
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
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
name|void
name|send
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Message
name|message
parameter_list|,
name|boolean
name|persistent
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeToLive
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|deliveryMode
init|=
name|persistent
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
name|getProducer
argument_list|()
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
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
name|Connection
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
specifier|protected
specifier|static
specifier|synchronized
name|void
name|initConnectionFactory
parameter_list|(
name|ServletContext
name|servletContext
parameter_list|)
block|{
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
operator|(
name|ConnectionFactory
operator|)
name|servletContext
operator|.
name|getAttribute
argument_list|(
name|CONNECTION_FACTORY_ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|String
name|brokerURL
init|=
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|BROKER_URL_INIT_PARAM
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Value of: "
operator|+
name|BROKER_URL_INIT_PARAM
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"missing brokerURL (specified via "
operator|+
name|BROKER_URL_INIT_PARAM
operator|+
literal|" init-Param"
argument_list|)
throw|;
block|}
name|ActiveMQConnectionFactory
name|amqfactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURL
argument_list|)
decl_stmt|;
comment|// Set prefetch policy for factory
if|if
condition|(
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|CONNECTION_FACTORY_PREFETCH_PARAM
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|int
name|prefetch
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|CONNECTION_FACTORY_PREFETCH_PARAM
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|amqfactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
name|prefetch
argument_list|)
expr_stmt|;
block|}
comment|// Set optimize acknowledge setting
if|if
condition|(
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|boolean
name|optimizeAck
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|servletContext
operator|.
name|getInitParameter
argument_list|(
name|CONNECTION_FACTORY_OPTIMIZE_ACK_PARAM
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
name|amqfactory
operator|.
name|setOptimizeAcknowledge
argument_list|(
name|optimizeAck
argument_list|)
expr_stmt|;
block|}
name|factory
operator|=
name|amqfactory
expr_stmt|;
name|servletContext
operator|.
name|setAttribute
argument_list|(
name|CONNECTION_FACTORY_ATTRIBUTE
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|CamelContext
name|getCamelContext
parameter_list|()
block|{
if|if
condition|(
name|camelContext
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating camel context"
argument_list|)
expr_stmt|;
name|camelContext
operator|=
operator|new
name|DefaultCamelContext
argument_list|()
expr_stmt|;
name|ActiveMQConfiguration
name|conf
init|=
operator|new
name|ActiveMQConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|PooledConnectionFactory
argument_list|(
operator|(
name|ActiveMQConnectionFactory
operator|)
name|factory
argument_list|)
argument_list|)
expr_stmt|;
name|ActiveMQComponent
name|component
init|=
operator|new
name|ActiveMQComponent
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|camelContext
operator|.
name|addComponent
argument_list|(
literal|"activemq"
argument_list|,
name|component
argument_list|)
expr_stmt|;
block|}
return|return
name|camelContext
return|;
block|}
specifier|public
specifier|synchronized
name|ProducerTemplate
name|getProducerTemplate
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerTemplate
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating producer template"
argument_list|)
expr_stmt|;
name|producerTemplate
operator|=
name|getCamelContext
argument_list|()
operator|.
name|createProducerTemplate
argument_list|()
expr_stmt|;
name|producerTemplate
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
name|producerTemplate
return|;
block|}
specifier|public
specifier|synchronized
name|MessageProducer
name|getProducer
parameter_list|()
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
return|return
name|producer
return|;
block|}
specifier|public
name|void
name|setProducer
parameter_list|(
name|MessageProducer
name|producer
parameter_list|)
block|{
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|MessageConsumer
name|getConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getConsumer
argument_list|(
name|destination
argument_list|,
name|selector
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|MessageConsumer
name|getConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageConsumer
name|consumer
init|=
name|consumers
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
operator|&&
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
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|consumers
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
specifier|public
specifier|synchronized
name|void
name|closeConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageConsumer
name|consumer
init|=
name|consumers
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumers
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|instanceof
name|MessageAvailableConsumer
condition|)
block|{
operator|(
operator|(
name|MessageAvailableConsumer
operator|)
name|consumer
operator|)
operator|.
name|setAvailableListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|List
argument_list|<
name|MessageConsumer
argument_list|>
name|getConsumers
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|MessageConsumer
argument_list|>
argument_list|(
name|consumers
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Session
name|createSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
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
specifier|public
name|Semaphore
name|getSemaphore
parameter_list|()
block|{
return|return
name|semaphore
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
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sessionDidActivate
parameter_list|(
name|HttpSessionEvent
name|event
parameter_list|)
block|{     }
specifier|public
name|void
name|valueBound
parameter_list|(
name|HttpSessionBindingEvent
name|event
parameter_list|)
block|{     }
specifier|public
name|void
name|valueUnbound
parameter_list|(
name|HttpSessionBindingEvent
name|event
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|static
name|WebClient
name|createWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|WebClient
argument_list|()
return|;
block|}
block|}
end_class

end_unit

