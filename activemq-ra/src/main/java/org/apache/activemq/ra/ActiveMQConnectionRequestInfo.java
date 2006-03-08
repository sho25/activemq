begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQPrefetchPolicy
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
name|RedeliveryPolicy
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
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ConnectionRequestInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  *   * Must override equals and hashCode (JCA spec 16.4)  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionRequestInfo
implements|implements
name|ConnectionRequestInfo
implements|,
name|Serializable
implements|,
name|Cloneable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5754338187296859149L
decl_stmt|;
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|serverUrl
decl_stmt|;
specifier|private
name|String
name|clientid
decl_stmt|;
specifier|private
name|Boolean
name|useInboundSession
decl_stmt|;
specifier|private
name|RedeliveryPolicy
name|redeliveryPolicy
decl_stmt|;
specifier|private
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
decl_stmt|;
specifier|public
name|ActiveMQConnectionRequestInfo
name|copy
parameter_list|()
block|{
try|try
block|{
name|ActiveMQConnectionRequestInfo
name|answer
init|=
operator|(
name|ActiveMQConnectionRequestInfo
operator|)
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|redeliveryPolicy
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|redeliveryPolicy
operator|=
name|redeliveryPolicy
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not clone: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns true if this object will configure an ActiveMQConnectionFactory in any way      */
specifier|public
name|boolean
name|isConnectionFactoryConfigured
parameter_list|()
block|{
return|return
name|serverUrl
operator|!=
literal|null
operator|||
name|clientid
operator|!=
literal|null
operator|||
name|redeliveryPolicy
operator|!=
literal|null
operator|||
name|prefetchPolicy
operator|!=
literal|null
return|;
block|}
comment|/**      * Configures the given connection factory      */
specifier|public
name|void
name|configure
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|)
block|{
if|if
condition|(
name|serverUrl
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setBrokerURL
argument_list|(
name|serverUrl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clientid
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setClientID
argument_list|(
name|clientid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|redeliveryPolicy
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setRedeliveryPolicy
argument_list|(
name|redeliveryPolicy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefetchPolicy
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetchPolicy
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see javax.resource.spi.ConnectionRequestInfo#hashCode()      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|rc
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|useInboundSession
operator|!=
literal|null
condition|)
block|{
name|rc
operator|^=
name|useInboundSession
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|serverUrl
operator|!=
literal|null
condition|)
block|{
name|rc
operator|^=
name|serverUrl
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
comment|/**      * @see javax.resource.spi.ConnectionRequestInfo#equals(java.lang.Object)      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ActiveMQConnectionRequestInfo
name|i
init|=
operator|(
name|ActiveMQConnectionRequestInfo
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|notEqual
argument_list|(
name|serverUrl
argument_list|,
name|i
operator|.
name|serverUrl
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|notEqual
argument_list|(
name|useInboundSession
argument_list|,
name|i
operator|.
name|useInboundSession
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * @param i      * @return      */
specifier|private
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
comment|/**      * @return Returns the url.      */
specifier|public
name|String
name|getServerUrl
parameter_list|()
block|{
return|return
name|serverUrl
return|;
block|}
comment|/**      * @param url      *            The url to set.      */
specifier|public
name|void
name|setServerUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|serverUrl
operator|=
name|url
expr_stmt|;
block|}
comment|/**      * @return Returns the password.      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**      * @param password      *            The password to set.      */
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
comment|/**      * @return Returns the userid.      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
comment|/**      * @param userid      *            The userid to set.      */
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userid
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userid
expr_stmt|;
block|}
comment|/**      * @return Returns the clientid.      */
specifier|public
name|String
name|getClientid
parameter_list|()
block|{
return|return
name|clientid
return|;
block|}
comment|/**      * @param clientid      *            The clientid to set.      */
specifier|public
name|void
name|setClientid
parameter_list|(
name|String
name|clientid
parameter_list|)
block|{
name|this
operator|.
name|clientid
operator|=
name|clientid
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ActiveMQConnectionRequestInfo{ "
operator|+
literal|"userName = '"
operator|+
name|userName
operator|+
literal|"' "
operator|+
literal|", serverUrl = '"
operator|+
name|serverUrl
operator|+
literal|"' "
operator|+
literal|", clientid = '"
operator|+
name|clientid
operator|+
literal|"' "
operator|+
literal|", userName = '"
operator|+
name|userName
operator|+
literal|"' "
operator|+
literal|", useInboundSession = '"
operator|+
name|useInboundSession
operator|+
literal|"' "
operator|+
literal|" }"
return|;
block|}
specifier|public
name|Boolean
name|getUseInboundSession
parameter_list|()
block|{
return|return
name|useInboundSession
return|;
block|}
specifier|public
name|void
name|setUseInboundSession
parameter_list|(
name|Boolean
name|useInboundSession
parameter_list|)
block|{
name|this
operator|.
name|useInboundSession
operator|=
name|useInboundSession
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseInboundSessionEnabled
parameter_list|()
block|{
return|return
name|useInboundSession
operator|!=
literal|null
operator|&&
name|useInboundSession
operator|.
name|booleanValue
argument_list|()
return|;
block|}
specifier|public
name|Short
name|getRedeliveryBackOffMultiplier
parameter_list|()
block|{
return|return
operator|new
name|Short
argument_list|(
name|redeliveryPolicy
argument_list|()
operator|.
name|getBackOffMultiplier
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Long
name|getInitialRedeliveryDelay
parameter_list|()
block|{
return|return
operator|new
name|Long
argument_list|(
name|redeliveryPolicy
argument_list|()
operator|.
name|getInitialRedeliveryDelay
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getMaximumRedeliveries
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|redeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|getRedeliveryUseExponentialBackOff
parameter_list|()
block|{
return|return
operator|new
name|Boolean
argument_list|(
name|redeliveryPolicy
argument_list|()
operator|.
name|isUseExponentialBackOff
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setRedeliveryBackOffMultiplier
parameter_list|(
name|Short
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|redeliveryPolicy
argument_list|()
operator|.
name|setBackOffMultiplier
argument_list|(
name|value
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|value
operator|!=
literal|null
condition|)
block|{
name|redeliveryPolicy
argument_list|()
operator|.
name|setInitialRedeliveryDelay
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|value
operator|!=
literal|null
condition|)
block|{
name|redeliveryPolicy
argument_list|()
operator|.
name|setMaximumRedeliveries
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|value
operator|!=
literal|null
condition|)
block|{
name|redeliveryPolicy
argument_list|()
operator|.
name|setUseExponentialBackOff
argument_list|(
name|value
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Integer
name|getDurableTopicPrefetch
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|prefetchPolicy
argument_list|()
operator|.
name|getDurableTopicPrefetch
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getInputStreamPrefetch
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|prefetchPolicy
argument_list|()
operator|.
name|getInputStreamPrefetch
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getQueueBrowserPrefetch
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|prefetchPolicy
argument_list|()
operator|.
name|getQueueBrowserPrefetch
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getQueuePrefetch
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|prefetchPolicy
argument_list|()
operator|.
name|getQueuePrefetch
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Integer
name|getTopicPrefetch
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
name|prefetchPolicy
argument_list|()
operator|.
name|getTopicPrefetch
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setAllPrefetchValues
parameter_list|(
name|Integer
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setAll
argument_list|(
name|i
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|durableTopicPrefetch
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setDurableTopicPrefetch
argument_list|(
name|durableTopicPrefetch
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|inputStreamPrefetch
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setInputStreamPrefetch
argument_list|(
name|inputStreamPrefetch
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|queueBrowserPrefetch
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setQueueBrowserPrefetch
argument_list|(
name|queueBrowserPrefetch
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|queuePrefetch
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
name|queuePrefetch
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|topicPrefetch
operator|!=
literal|null
condition|)
block|{
name|prefetchPolicy
argument_list|()
operator|.
name|setTopicPrefetch
argument_list|(
name|topicPrefetch
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the redelivery policy; not using bean properties to avoid      * breaking compatibility with JCA configuration in J2EE      */
specifier|public
name|RedeliveryPolicy
name|redeliveryPolicy
parameter_list|()
block|{
if|if
condition|(
name|redeliveryPolicy
operator|==
literal|null
condition|)
block|{
name|redeliveryPolicy
operator|=
operator|new
name|RedeliveryPolicy
argument_list|()
expr_stmt|;
block|}
return|return
name|redeliveryPolicy
return|;
block|}
comment|/**      * Returns the prefetch policy; not using bean properties to avoid      * breaking compatibility with JCA configuration in J2EE      */
specifier|public
name|ActiveMQPrefetchPolicy
name|prefetchPolicy
parameter_list|()
block|{
if|if
condition|(
name|prefetchPolicy
operator|==
literal|null
condition|)
block|{
name|prefetchPolicy
operator|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
expr_stmt|;
block|}
return|return
name|prefetchPolicy
return|;
block|}
block|}
end_class

end_unit

