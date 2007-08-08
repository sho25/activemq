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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Used to cache up authorizations so that subsequent requests are faster.  *   * @version $Revision$  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|SecurityContext
block|{
specifier|public
specifier|static
specifier|final
name|SecurityContext
name|BROKER_SECURITY_CONTEXT
init|=
operator|new
name|SecurityContext
argument_list|(
literal|"ActiveMQBroker"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isBrokerContext
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Set
name|getPrincipals
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|String
name|userName
decl_stmt|;
specifier|final
name|ConcurrentHashMap
name|authorizedReadDests
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|final
name|ConcurrentHashMap
name|authorizedWriteDests
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|SecurityContext
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
name|isInOneOf
parameter_list|(
name|Set
name|allowedPrincipals
parameter_list|)
block|{
name|HashSet
name|set
init|=
operator|new
name|HashSet
argument_list|(
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
name|set
operator|.
name|retainAll
argument_list|(
name|allowedPrincipals
argument_list|)
expr_stmt|;
return|return
name|set
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|abstract
specifier|public
name|Set
name|getPrincipals
parameter_list|()
function_decl|;
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
name|ConcurrentHashMap
name|getAuthorizedReadDests
parameter_list|()
block|{
return|return
name|authorizedReadDests
return|;
block|}
specifier|public
name|ConcurrentHashMap
name|getAuthorizedWriteDests
parameter_list|()
block|{
return|return
name|authorizedWriteDests
return|;
block|}
specifier|public
name|boolean
name|isBrokerContext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

