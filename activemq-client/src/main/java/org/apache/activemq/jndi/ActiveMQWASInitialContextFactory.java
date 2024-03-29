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
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Map
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
name|NamingException
import|;
end_import

begin_comment
comment|/**  * A InitialContextFactory for WebSphere Generic JMS Provider.  *<p>  * Works on WebSphere 5.1. The reason for using this class is that custom  * property defined for Generic JMS Provider are passed to {@link InitialContextFactory}  * only if it begins with {@code java.naming} or {@code javax.naming} prefix.  * Additionally provider url for the JMS provider can not contain {@code ','}  * character that is necessary when the list of nodes is provided. So the role  * of this class is to transform properties before passing it to   * {@link ActiveMQInitialContextFactory}.  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQWASInitialContextFactory
extends|extends
name|ActiveMQInitialContextFactory
block|{
comment|/**      * @see javax.naming.spi.InitialContextFactory#getInitialContext(java.util.Hashtable)      */
specifier|public
name|Context
name|getInitialContext
parameter_list|(
name|Hashtable
name|environment
parameter_list|)
throws|throws
name|NamingException
block|{
return|return
name|super
operator|.
name|getInitialContext
argument_list|(
name|transformEnvironment
argument_list|(
name|environment
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Performs following transformation of properties:      *<ul>      *<li>(java.naming.queue.xxx.yyy=value) ->(queue.xxx/yyy=value)      *<li>(java.naming.topic.xxx.yyy=value) -> (topic.xxx/yyy=value)      *<li>(java.naming.connectionxxx=value) -> (connectionxxx=value)      *<li>(java.naming.provider.url=url1;url2) -> (java.naming.provider.url=url1,url2)      *<ul>      *      * @param environment properties for transformation      * @return environment after transformation      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|Hashtable
name|transformEnvironment
parameter_list|(
name|Hashtable
name|environment
parameter_list|)
block|{
name|Hashtable
name|environment1
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|Iterator
name|it
init|=
name|environment
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|instanceof
name|String
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|String
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"java.naming.queue."
argument_list|)
condition|)
block|{
name|String
name|key1
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"java.naming.queue."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|key1
operator|=
name|key1
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|environment1
operator|.
name|put
argument_list|(
literal|"queue."
operator|+
name|key1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"java.naming.topic."
argument_list|)
condition|)
block|{
name|String
name|key1
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"java.naming.topic."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|key1
operator|=
name|key1
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
name|environment1
operator|.
name|put
argument_list|(
literal|"topic."
operator|+
name|key1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"java.naming.connectionFactoryNames"
argument_list|)
condition|)
block|{
name|String
name|key1
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"java.naming."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|environment1
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"java.naming.connection"
argument_list|)
condition|)
block|{
name|String
name|key1
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"java.naming."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|environment1
operator|.
name|put
argument_list|(
name|key1
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|)
condition|)
block|{
comment|// Websphere administration console does not accept the , character
comment|// in provider url, so ; must be used all ; to ,
name|value
operator|=
name|value
operator|.
name|replace
argument_list|(
literal|';'
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|environment1
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|environment1
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|environment1
return|;
block|}
block|}
end_class

end_unit

