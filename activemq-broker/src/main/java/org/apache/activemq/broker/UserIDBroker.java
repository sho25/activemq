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
name|broker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|command
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
name|jaas
operator|.
name|UserPrincipal
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
name|SecurityContext
import|;
end_import

begin_comment
comment|/**  * This broker filter will append the producer's user ID into the JMSXUserID header  * to allow folks to know reliably who the user was who produced a message.  * Note that you cannot trust the client, especially if working over the internet  * as they can spoof headers to be anything they like.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|UserIDBroker
extends|extends
name|BrokerFilter
block|{
name|boolean
name|useAuthenticatePrincipal
init|=
literal|false
decl_stmt|;
specifier|public
name|UserIDBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ConnectionContext
name|context
init|=
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
name|context
operator|.
name|getUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isUseAuthenticatePrincipal
argument_list|()
condition|)
block|{
name|SecurityContext
name|securityContext
init|=
name|context
operator|.
name|getSecurityContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|securityContext
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|?
argument_list|>
name|principals
init|=
name|securityContext
operator|.
name|getPrincipals
argument_list|()
decl_stmt|;
if|if
condition|(
name|principals
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|candidate
range|:
name|principals
control|)
block|{
if|if
condition|(
name|candidate
operator|instanceof
name|UserPrincipal
condition|)
block|{
name|userID
operator|=
operator|(
operator|(
name|UserPrincipal
operator|)
name|candidate
operator|)
operator|.
name|getName
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
name|messageSend
operator|.
name|setUserID
argument_list|(
name|userID
argument_list|)
expr_stmt|;
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseAuthenticatePrincipal
parameter_list|()
block|{
return|return
name|useAuthenticatePrincipal
return|;
block|}
specifier|public
name|void
name|setUseAuthenticatePrincipal
parameter_list|(
name|boolean
name|useAuthenticatePrincipal
parameter_list|)
block|{
name|this
operator|.
name|useAuthenticatePrincipal
operator|=
name|useAuthenticatePrincipal
expr_stmt|;
block|}
block|}
end_class

end_unit
