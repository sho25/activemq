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
name|JMSException
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
name|ActiveMQConnection
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
name|ActiveMQSslConnectionFactory
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
comment|/**  * Abstract base class providing support for creating physical  * connections to an ActiveMQ instance.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionSupport
block|{
specifier|private
name|ActiveMQConnectionRequestInfo
name|info
init|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
decl_stmt|;
specifier|protected
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Creates a factory for obtaining physical connections to an Active MQ      * broker. The factory is configured with the given configuration information.      *       * @param connectionRequestInfo the configuration request information      * @param activationSpec      * @return the connection factory      * @throws java.lang.IllegalArgumentException if the server URL given in the      * configuration information is not a valid URL      */
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|,
name|MessageActivationSpec
name|activationSpec
parameter_list|)
block|{
comment|//ActiveMQSslConnectionFactory defaults to TCP anyway
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQSslConnectionFactory
argument_list|()
decl_stmt|;
name|connectionRequestInfo
operator|.
name|configure
argument_list|(
name|factory
argument_list|,
name|activationSpec
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
comment|/**      * Creates a new physical connection to an Active MQ broker identified by given      * connection request information.      *       * @param connectionRequestInfo the connection request information identifying the broker and any      * required connection parameters, e.g. username/password      * @return the physical connection      * @throws JMSException if the connection could not be established      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|makeConnection
argument_list|(
name|connectionRequestInfo
argument_list|,
name|createConnectionFactory
argument_list|(
name|connectionRequestInfo
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Creates a new physical connection to an Active MQ broker using a given      * connection factory and credentials supplied in connection request information.      *       * @param connectionRequestInfo the connection request information containing the credentials to use      * for the connection request      * @return the physical connection      * @throws JMSException if the connection could not be established      */
specifier|public
name|ActiveMQConnection
name|makeConnection
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|userName
init|=
name|connectionRequestInfo
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|password
init|=
name|connectionRequestInfo
operator|.
name|getPassword
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|physicalConnection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|connectionFactory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|connectionRequestInfo
operator|.
name|getClientid
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
operator|&&
name|clientId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|physicalConnection
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
return|return
name|physicalConnection
return|;
block|}
comment|/**      * Gets the connection request information.      *       * @return the connection request information      */
specifier|public
name|ActiveMQConnectionRequestInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|/**      * Sets the connection request information as a whole.      *       * @param connectionRequestInfo the connection request information      */
specifier|protected
name|void
name|setInfo
parameter_list|(
name|ActiveMQConnectionRequestInfo
name|connectionRequestInfo
parameter_list|)
block|{
name|info
operator|=
name|connectionRequestInfo
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
name|this
operator|+
literal|", setting [info] to: "
operator|+
name|info
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|notEqual
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|==
literal|null
operator|^
name|o2
operator|==
literal|null
operator|)
operator|||
operator|(
name|o1
operator|!=
literal|null
operator|&&
operator|!
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
operator|)
return|;
block|}
specifier|protected
name|String
name|emptyToNull
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
block|}
specifier|protected
name|String
name|defaultValue
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
return|return
name|defaultValue
return|;
block|}
comment|// ///////////////////////////////////////////////////////////////////////
comment|//
comment|// Java Bean getters and setters for this ResourceAdapter class.
comment|//
comment|// ///////////////////////////////////////////////////////////////////////
comment|/**      * @return client id      */
specifier|public
name|String
name|getClientid
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getClientid
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @param clientid      */
specifier|public
name|void
name|setClientid
parameter_list|(
name|String
name|clientid
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [clientid] to: "
operator|+
name|clientid
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setClientid
argument_list|(
name|clientid
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return password      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @param password      */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [password] property"
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return server URL      */
specifier|public
name|String
name|getServerUrl
parameter_list|()
block|{
return|return
name|info
operator|.
name|getServerUrl
argument_list|()
return|;
block|}
comment|/**      * @param url      */
specifier|public
name|void
name|setServerUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [serverUrl] to: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setServerUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTrustStore
parameter_list|(
name|String
name|trustStore
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [trustStore] to: "
operator|+
name|trustStore
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setTrustStore
argument_list|(
name|trustStore
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTrustStorePassword
parameter_list|(
name|String
name|trustStorePassword
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [trustStorePassword] to: "
operator|+
name|trustStorePassword
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setTrustStorePassword
argument_list|(
name|trustStorePassword
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyStore
parameter_list|(
name|String
name|keyStore
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [keyStore] to: "
operator|+
name|keyStore
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setKeyStore
argument_list|(
name|keyStore
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyStorePassword
parameter_list|(
name|String
name|keyStorePassword
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [keyStorePassword] to: "
operator|+
name|keyStorePassword
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setKeyStorePassword
argument_list|(
name|keyStorePassword
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyStoreKeyPassword
parameter_list|(
name|String
name|keyStoreKeyPassword
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [keyStoreKeyPassword] to: "
operator|+
name|keyStoreKeyPassword
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setKeyStoreKeyPassword
argument_list|(
name|keyStoreKeyPassword
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return user name      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|emptyToNull
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @param userid      */
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userid
parameter_list|)
block|{
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
literal|"setting [userName] to: "
operator|+
name|userid
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setUserName
argument_list|(
name|userid
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return durable topic prefetch      */
specifier|public
name|Integer
name|getDurableTopicPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getDurableTopicPrefetch
argument_list|()
return|;
block|}
comment|/**      * @param optimizeDurableTopicPrefetch      */
specifier|public
name|void
name|setOptimizeDurableTopicPrefetch
parameter_list|(
name|Integer
name|optimizeDurableTopicPrefetch
parameter_list|)
block|{
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
literal|"setting [optimizeDurableTopicPrefetch] to: "
operator|+
name|optimizeDurableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setOptimizeDurableTopicPrefetch
argument_list|(
name|optimizeDurableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return durable topic prefetch      */
specifier|public
name|Integer
name|getOptimizeDurableTopicPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getOptimizeDurableTopicPrefetch
argument_list|()
return|;
block|}
comment|/**      * @param durableTopicPrefetch      */
specifier|public
name|void
name|setDurableTopicPrefetch
parameter_list|(
name|Integer
name|durableTopicPrefetch
parameter_list|)
block|{
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
literal|"setting [durableTopicPrefetch] to: "
operator|+
name|durableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|durableTopicPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return initial redelivery delay      */
specifier|public
name|Long
name|getInitialRedeliveryDelay
parameter_list|()
block|{
return|return
name|info
operator|.
name|getInitialRedeliveryDelay
argument_list|()
return|;
block|}
comment|/**      * @param value      */
specifier|public
name|void
name|setInitialRedeliveryDelay
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
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
literal|"setting [initialRedeliveryDelay] to: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setInitialRedeliveryDelay
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return initial redelivery delay      */
specifier|public
name|Long
name|getMaximumRedeliveryDelay
parameter_list|()
block|{
return|return
name|info
operator|.
name|getMaximumRedeliveryDelay
argument_list|()
return|;
block|}
comment|/**      * @param value      */
specifier|public
name|void
name|setMaximumRedeliveryDelay
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
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
literal|"setting [maximumRedeliveryDelay] to: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setMaximumRedeliveryDelay
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return input stream prefetch      */
specifier|public
name|Integer
name|getInputStreamPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getInputStreamPrefetch
argument_list|()
return|;
block|}
comment|/**      * @param inputStreamPrefetch      */
specifier|public
name|void
name|setInputStreamPrefetch
parameter_list|(
name|Integer
name|inputStreamPrefetch
parameter_list|)
block|{
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
literal|"setting [inputStreamPrefetch] to: "
operator|+
name|inputStreamPrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setInputStreamPrefetch
argument_list|(
name|inputStreamPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return maximum redeliveries      */
specifier|public
name|Integer
name|getMaximumRedeliveries
parameter_list|()
block|{
return|return
name|info
operator|.
name|getMaximumRedeliveries
argument_list|()
return|;
block|}
comment|/**      * @param value      */
specifier|public
name|void
name|setMaximumRedeliveries
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
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
literal|"setting [maximumRedeliveries] to: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setMaximumRedeliveries
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return queue browser prefetch      */
specifier|public
name|Integer
name|getQueueBrowserPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getQueueBrowserPrefetch
argument_list|()
return|;
block|}
comment|/**      * @param queueBrowserPrefetch      */
specifier|public
name|void
name|setQueueBrowserPrefetch
parameter_list|(
name|Integer
name|queueBrowserPrefetch
parameter_list|)
block|{
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
literal|"setting [queueBrowserPrefetch] to: "
operator|+
name|queueBrowserPrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setQueueBrowserPrefetch
argument_list|(
name|queueBrowserPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return queue prefetch      */
specifier|public
name|Integer
name|getQueuePrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getQueuePrefetch
argument_list|()
return|;
block|}
comment|/**      * @param queuePrefetch      */
specifier|public
name|void
name|setQueuePrefetch
parameter_list|(
name|Integer
name|queuePrefetch
parameter_list|)
block|{
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
literal|"setting [queuePrefetch] to: "
operator|+
name|queuePrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setQueuePrefetch
argument_list|(
name|queuePrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return redelivery backoff multiplier      */
specifier|public
name|Double
name|getRedeliveryBackOffMultiplier
parameter_list|()
block|{
return|return
name|info
operator|.
name|getRedeliveryBackOffMultiplier
argument_list|()
return|;
block|}
comment|/**      * @param value      */
specifier|public
name|void
name|setRedeliveryBackOffMultiplier
parameter_list|(
name|Double
name|value
parameter_list|)
block|{
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
literal|"setting [redeliveryBackOffMultiplier] to: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setRedeliveryBackOffMultiplier
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return redelivery use exponential backoff      */
specifier|public
name|Boolean
name|getRedeliveryUseExponentialBackOff
parameter_list|()
block|{
return|return
name|info
operator|.
name|getRedeliveryUseExponentialBackOff
argument_list|()
return|;
block|}
comment|/**      * @param value      */
specifier|public
name|void
name|setRedeliveryUseExponentialBackOff
parameter_list|(
name|Boolean
name|value
parameter_list|)
block|{
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
literal|"setting [redeliveryUseExponentialBackOff] to: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setRedeliveryUseExponentialBackOff
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return topic prefetch      */
specifier|public
name|Integer
name|getTopicPrefetch
parameter_list|()
block|{
return|return
name|info
operator|.
name|getTopicPrefetch
argument_list|()
return|;
block|}
comment|/**      * @param topicPrefetch      */
specifier|public
name|void
name|setTopicPrefetch
parameter_list|(
name|Integer
name|topicPrefetch
parameter_list|)
block|{
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
literal|"setting [topicPrefetch] to: "
operator|+
name|topicPrefetch
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setTopicPrefetch
argument_list|(
name|topicPrefetch
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param i      */
specifier|public
name|void
name|setAllPrefetchValues
parameter_list|(
name|Integer
name|i
parameter_list|)
block|{
name|info
operator|.
name|setAllPrefetchValues
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return use inbound session enabled      */
specifier|public
name|boolean
name|isUseInboundSessionEnabled
parameter_list|()
block|{
return|return
name|info
operator|.
name|isUseInboundSessionEnabled
argument_list|()
return|;
block|}
comment|/**      * @return use inbound session      */
specifier|public
name|Boolean
name|getUseInboundSession
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUseInboundSession
argument_list|()
return|;
block|}
comment|/**      * @param useInboundSession      */
specifier|public
name|void
name|setUseInboundSession
parameter_list|(
name|Boolean
name|useInboundSession
parameter_list|)
block|{
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
literal|"setting [useInboundSession] to: "
operator|+
name|useInboundSession
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setUseInboundSession
argument_list|(
name|useInboundSession
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseSessionArgs
parameter_list|()
block|{
return|return
name|info
operator|.
name|isUseSessionArgs
argument_list|()
return|;
block|}
specifier|public
name|Boolean
name|getUseSessionArgs
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUseSessionArgs
argument_list|()
return|;
block|}
comment|/**      * if true, calls to managed connection factory.connection.createSession will      * respect the passed in args. When false (default) the args are ignored b/c      * the container will do transaction demarcation via xa or local transaction rar      * contracts.      * This option is useful when a managed connection is used in plain jms mode      * and a jms transacted session session is required.      * @param useSessionArgs      */
specifier|public
name|void
name|setUseSessionArgs
parameter_list|(
name|Boolean
name|useSessionArgs
parameter_list|)
block|{
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
name|this
operator|+
literal|", setting [useSessionArgs] to: "
operator|+
name|useSessionArgs
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setUseSessionArgs
argument_list|(
name|useSessionArgs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

