begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Properties
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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicConnectionFactory
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
name|org
operator|.
name|activemq
operator|.
name|management
operator|.
name|JMSStatsImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|management
operator|.
name|StatsCapable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|management
operator|.
name|StatsImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|Transport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|JMSExceptionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|URISupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|URISupport
operator|.
name|CompositeData
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
name|Executor
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
name|ScheduledThreadPoolExecutor
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
name|ThreadFactory
import|;
end_import

begin_comment
comment|/**  * A ConnectionFactory is an an Administered object, and is used for creating  * Connections.<p/> This class also implements QueueConnectionFactory and  * TopicConnectionFactory. You can use this connection to create both  * QueueConnections and TopicConnections.  *   * @version $Revision: 1.9 $  * @see javax.jms.ConnectionFactory  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionFactory
implements|implements
name|ConnectionFactory
implements|,
name|QueueConnectionFactory
implements|,
name|TopicConnectionFactory
implements|,
name|StatsCapable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_BROKER_URL
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_USER
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PASSWORD
init|=
literal|null
decl_stmt|;
specifier|protected
name|URI
name|brokerURL
decl_stmt|;
specifier|protected
name|String
name|userName
decl_stmt|;
specifier|protected
name|String
name|password
decl_stmt|;
specifier|protected
name|String
name|clientID
decl_stmt|;
specifier|protected
name|boolean
name|useEmbeddedBroker
decl_stmt|;
comment|// optimization flags
specifier|private
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|disableTimeStampsByDefault
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|onSendPrepareMessageBody
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|optimizedMessageDispatch
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|copyMessageOnSend
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useCompression
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|objectMessageSerializationDefered
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|asyncDispatch
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useAsyncSend
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|useRetroactiveConsumer
decl_stmt|;
name|JMSStatsImpl
name|factoryStats
init|=
operator|new
name|JMSStatsImpl
argument_list|()
decl_stmt|;
specifier|static
specifier|protected
specifier|final
name|Executor
name|DEFAULT_CONNECTION_EXECUTOR
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|5
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|run
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|run
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setPriority
argument_list|(
name|ThreadPriorities
operator|.
name|INBOUND_CLIENT_CONNECTION
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// /////////////////////////////////////////////
comment|//
comment|// ConnectionFactory, QueueConnectionFactory, TopicConnectionFactory Methods
comment|//
comment|// /////////////////////////////////////////////
specifier|public
name|ActiveMQConnectionFactory
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_BROKER_URL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnectionFactory
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
argument_list|(
name|createURI
argument_list|(
name|brokerURL
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param brokerURL      * @return      * @throws URISyntaxException      */
specifier|private
specifier|static
name|URI
name|createURI
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|brokerURL
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IllegalArgumentException
operator|)
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid broker URI: "
operator|+
name|brokerURL
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|ActiveMQConnectionFactory
parameter_list|(
name|URI
name|brokerURL
parameter_list|)
block|{
name|setBrokerURL
argument_list|(
name|brokerURL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnectionFactory
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|URI
name|brokerURL
parameter_list|)
block|{
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|setBrokerURL
argument_list|(
name|brokerURL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnectionFactory
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|brokerURL
parameter_list|)
block|{
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|setBrokerURL
argument_list|(
name|brokerURL
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the Connection.      */
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * @return Returns the Connection.      */
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * @return Returns the QueueConnection.      * @throws JMSException      */
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * @return Returns the QueueConnection.      */
specifier|public
name|QueueConnection
name|createQueueConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * @return Returns the TopicConnection.      * @throws JMSException      */
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
comment|/**      * @return Returns the TopicConnection.      */
specifier|public
name|TopicConnection
name|createTopicConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createActiveMQConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|StatsImpl
name|getStats
parameter_list|()
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
comment|// /////////////////////////////////////////////
comment|//
comment|// Implementation methods.
comment|//
comment|// /////////////////////////////////////////////
comment|/**      * @return Returns the Connection.      */
specifier|private
name|ActiveMQConnection
name|createActiveMQConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|brokerURL
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"brokerURL not set."
argument_list|)
throw|;
block|}
name|Transport
name|transport
decl_stmt|;
try|try
block|{
name|transport
operator|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|brokerURL
argument_list|,
name|DEFAULT_CONNECTION_EXECUTOR
argument_list|)
expr_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|new
name|ActiveMQConnection
argument_list|(
name|transport
argument_list|,
name|userName
argument_list|,
name|password
argument_list|,
name|factoryStats
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setPrefetchPolicy
argument_list|(
name|getPrefetchPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setDisableTimeStampsByDefault
argument_list|(
name|isDisableTimeStampsByDefault
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setOnSendPrepareMessageBody
argument_list|(
name|isOnSendPrepareMessageBody
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setOptimizedMessageDispatch
argument_list|(
name|isOptimizedMessageDispatch
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setCopyMessageOnSend
argument_list|(
name|isCopyMessageOnSend
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setUseCompression
argument_list|(
name|isUseCompression
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
name|isObjectMessageSerializationDefered
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setAsyncDispatch
argument_list|(
name|isAsyncDispatch
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setUseAsyncSend
argument_list|(
name|isUseAsyncSend
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setUseRetroactiveConsumer
argument_list|(
name|isUseRetroactiveConsumer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
literal|"Could not connect to broker URL: "
operator|+
name|brokerURL
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// /////////////////////////////////////////////
comment|//
comment|// Property Accessors
comment|//
comment|// /////////////////////////////////////////////
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|brokerURL
operator|==
literal|null
condition|?
literal|null
else|:
name|brokerURL
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|setBrokerURL
parameter_list|(
name|String
name|brokerURL
parameter_list|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|createURI
argument_list|(
name|brokerURL
argument_list|)
expr_stmt|;
comment|// Use all the properties prefixed with 'jms.' to set the connection factory
comment|// options.
if|if
condition|(
name|this
operator|.
name|brokerURL
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// It might be a standard URI or...
try|try
block|{
name|Map
name|map
init|=
name|URISupport
operator|.
name|parseQuery
argument_list|(
name|this
operator|.
name|brokerURL
operator|.
name|getQuery
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|map
argument_list|,
literal|"jms."
argument_list|)
condition|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|URISupport
operator|.
name|createRemainingURI
argument_list|(
name|this
operator|.
name|brokerURL
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{             }
block|}
else|else
block|{
comment|// It might be a composite URI.
try|try
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|this
operator|.
name|brokerURL
argument_list|)
decl_stmt|;
if|if
condition|(
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
argument_list|,
literal|"jms."
argument_list|)
condition|)
block|{
name|this
operator|.
name|brokerURL
operator|=
name|data
operator|.
name|toURI
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|public
name|String
name|getClientID
parameter_list|()
block|{
return|return
name|clientID
return|;
block|}
specifier|public
name|void
name|setClientID
parameter_list|(
name|String
name|clientID
parameter_list|)
block|{
name|this
operator|.
name|clientID
operator|=
name|clientID
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCopyMessageOnSend
parameter_list|()
block|{
return|return
name|copyMessageOnSend
return|;
block|}
specifier|public
name|void
name|setCopyMessageOnSend
parameter_list|(
name|boolean
name|copyMessageOnSend
parameter_list|)
block|{
name|this
operator|.
name|copyMessageOnSend
operator|=
name|copyMessageOnSend
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDisableTimeStampsByDefault
parameter_list|()
block|{
return|return
name|disableTimeStampsByDefault
return|;
block|}
specifier|public
name|void
name|setDisableTimeStampsByDefault
parameter_list|(
name|boolean
name|disableTimeStampsByDefault
parameter_list|)
block|{
name|this
operator|.
name|disableTimeStampsByDefault
operator|=
name|disableTimeStampsByDefault
expr_stmt|;
block|}
specifier|public
name|boolean
name|isOptimizedMessageDispatch
parameter_list|()
block|{
return|return
name|optimizedMessageDispatch
return|;
block|}
specifier|public
name|void
name|setOptimizedMessageDispatch
parameter_list|(
name|boolean
name|optimizedMessageDispatch
parameter_list|)
block|{
name|this
operator|.
name|optimizedMessageDispatch
operator|=
name|optimizedMessageDispatch
expr_stmt|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
specifier|public
name|ActiveMQPrefetchPolicy
name|getPrefetchPolicy
parameter_list|()
block|{
return|return
name|prefetchPolicy
return|;
block|}
specifier|public
name|void
name|setPrefetchPolicy
parameter_list|(
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
parameter_list|)
block|{
name|this
operator|.
name|prefetchPolicy
operator|=
name|prefetchPolicy
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseAsyncSend
parameter_list|()
block|{
return|return
name|useAsyncSend
return|;
block|}
specifier|public
name|void
name|setUseAsyncSend
parameter_list|(
name|boolean
name|useAsyncSend
parameter_list|)
block|{
name|this
operator|.
name|useAsyncSend
operator|=
name|useAsyncSend
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseEmbeddedBroker
parameter_list|()
block|{
return|return
name|useEmbeddedBroker
return|;
block|}
specifier|public
name|void
name|setUseEmbeddedBroker
parameter_list|(
name|boolean
name|useEmbeddedBroker
parameter_list|)
block|{
name|this
operator|.
name|useEmbeddedBroker
operator|=
name|useEmbeddedBroker
expr_stmt|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseRetroactiveConsumer
parameter_list|()
block|{
return|return
name|useRetroactiveConsumer
return|;
block|}
comment|/**      * Sets whether or not retroactive consumers are enabled. Retroactive consumers allow      * non-durable topic subscribers to receive old messages that were published before the      * non-durable subscriber started.      */
specifier|public
name|void
name|setUseRetroactiveConsumer
parameter_list|(
name|boolean
name|useRetroactiveConsumer
parameter_list|)
block|{
name|this
operator|.
name|useRetroactiveConsumer
operator|=
name|useRetroactiveConsumer
expr_stmt|;
block|}
comment|/**      * set the properties for this instance as retrieved from JNDI      *       * @param properties      */
specifier|public
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|URISyntaxException
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|properties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|String
name|temp
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|temp
operator|==
literal|null
operator|||
name|temp
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|temp
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"brokerURL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|temp
operator|!=
literal|null
operator|&&
name|temp
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|setBrokerURL
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isOnSendPrepareMessageBody
parameter_list|()
block|{
return|return
name|onSendPrepareMessageBody
return|;
block|}
specifier|public
name|void
name|setOnSendPrepareMessageBody
parameter_list|(
name|boolean
name|onSendPrepareMessageBody
parameter_list|)
block|{
name|this
operator|.
name|onSendPrepareMessageBody
operator|=
name|onSendPrepareMessageBody
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseCompression
parameter_list|()
block|{
return|return
name|useCompression
return|;
block|}
specifier|public
name|void
name|setUseCompression
parameter_list|(
name|boolean
name|useCompression
parameter_list|)
block|{
name|this
operator|.
name|useCompression
operator|=
name|useCompression
expr_stmt|;
block|}
specifier|public
name|boolean
name|isObjectMessageSerializationDefered
parameter_list|()
block|{
return|return
name|objectMessageSerializationDefered
return|;
block|}
specifier|public
name|void
name|setObjectMessageSerializationDefered
parameter_list|(
name|boolean
name|objectMessageSerializationDefered
parameter_list|)
block|{
name|this
operator|.
name|objectMessageSerializationDefered
operator|=
name|objectMessageSerializationDefered
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAsyncDispatch
parameter_list|()
block|{
return|return
name|asyncDispatch
return|;
block|}
specifier|public
name|void
name|setAsyncDispatch
parameter_list|(
name|boolean
name|asyncDispatch
parameter_list|)
block|{
name|this
operator|.
name|asyncDispatch
operator|=
name|asyncDispatch
expr_stmt|;
block|}
block|}
end_class

end_unit

