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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMapEntry
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
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Represents an entry in a {@link DefaultAuthorizationMap} for assigning  * different operations (read, write, admin) of user roles to a specific  * destination or a hierarchical wildcard area of destinations.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
class|class
name|AuthorizationEntry
extends|extends
name|DestinationMapEntry
block|{
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|readACLs
init|=
name|emptySet
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|writeACLs
init|=
name|emptySet
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|adminACLs
init|=
name|emptySet
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|adminRoles
decl_stmt|;
specifier|protected
name|String
name|readRoles
decl_stmt|;
specifier|protected
name|String
name|writeRoles
decl_stmt|;
specifier|private
name|String
name|groupClass
decl_stmt|;
specifier|public
name|String
name|getGroupClass
parameter_list|()
block|{
return|return
name|groupClass
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|emptySet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
specifier|public
name|void
name|setGroupClass
parameter_list|(
name|String
name|groupClass
parameter_list|)
block|{
name|this
operator|.
name|groupClass
operator|=
name|groupClass
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|Object
argument_list|>
name|getAdminACLs
parameter_list|()
block|{
return|return
name|adminACLs
return|;
block|}
specifier|public
name|void
name|setAdminACLs
parameter_list|(
name|Set
argument_list|<
name|Object
argument_list|>
name|adminACLs
parameter_list|)
block|{
name|this
operator|.
name|adminACLs
operator|=
name|adminACLs
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|Object
argument_list|>
name|getReadACLs
parameter_list|()
block|{
return|return
name|readACLs
return|;
block|}
specifier|public
name|void
name|setReadACLs
parameter_list|(
name|Set
argument_list|<
name|Object
argument_list|>
name|readACLs
parameter_list|)
block|{
name|this
operator|.
name|readACLs
operator|=
name|readACLs
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|Object
argument_list|>
name|getWriteACLs
parameter_list|()
block|{
return|return
name|writeACLs
return|;
block|}
specifier|public
name|void
name|setWriteACLs
parameter_list|(
name|Set
argument_list|<
name|Object
argument_list|>
name|writeACLs
parameter_list|)
block|{
name|this
operator|.
name|writeACLs
operator|=
name|writeACLs
expr_stmt|;
block|}
comment|// helper methods for easier configuration in Spring
comment|// ACLs are already set in the afterPropertiesSet method to ensure that
comment|// groupClass is set first before
comment|// calling parceACLs() on any of the roles. We still need to add the call to
comment|// parceACLs inside the helper
comment|// methods for instances where we configure security programatically without
comment|// using xbean
comment|// -------------------------------------------------------------------------
specifier|public
name|void
name|setAdmin
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|adminRoles
operator|=
name|roles
expr_stmt|;
name|setAdminACLs
argument_list|(
name|parseACLs
argument_list|(
name|adminRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setRead
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|readRoles
operator|=
name|roles
expr_stmt|;
name|setReadACLs
argument_list|(
name|parseACLs
argument_list|(
name|readRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setWrite
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|writeRoles
operator|=
name|roles
expr_stmt|;
name|setWriteACLs
argument_list|(
name|parseACLs
argument_list|(
name|writeRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Set
argument_list|<
name|Object
argument_list|>
name|parseACLs
parameter_list|(
name|String
name|roles
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|answer
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|iter
init|=
operator|new
name|StringTokenizer
argument_list|(
name|roles
argument_list|,
literal|","
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iter
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|groupClass
init|=
operator|(
name|this
operator|.
name|groupClass
operator|!=
literal|null
condition|?
name|this
operator|.
name|groupClass
else|:
name|DefaultAuthorizationMap
operator|.
name|DEFAULT_GROUP_CLASS
operator|)
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
name|DefaultAuthorizationMap
operator|.
name|createGroupPrincipal
argument_list|(
name|name
argument_list|,
name|groupClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
annotation|@
name|Override
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AuthorizationEntry
operator|)
condition|)
return|return
literal|false
return|;
name|AuthorizationEntry
name|that
init|=
operator|(
name|AuthorizationEntry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|adminACLs
operator|!=
literal|null
condition|?
operator|!
name|adminACLs
operator|.
name|equals
argument_list|(
name|that
operator|.
name|adminACLs
argument_list|)
else|:
name|that
operator|.
name|adminACLs
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|adminRoles
operator|!=
literal|null
condition|?
operator|!
name|adminRoles
operator|.
name|equals
argument_list|(
name|that
operator|.
name|adminRoles
argument_list|)
else|:
name|that
operator|.
name|adminRoles
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|groupClass
operator|!=
literal|null
condition|?
operator|!
name|groupClass
operator|.
name|equals
argument_list|(
name|that
operator|.
name|groupClass
argument_list|)
else|:
name|that
operator|.
name|groupClass
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|readACLs
operator|!=
literal|null
condition|?
operator|!
name|readACLs
operator|.
name|equals
argument_list|(
name|that
operator|.
name|readACLs
argument_list|)
else|:
name|that
operator|.
name|readACLs
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|readRoles
operator|!=
literal|null
condition|?
operator|!
name|readRoles
operator|.
name|equals
argument_list|(
name|that
operator|.
name|readRoles
argument_list|)
else|:
name|that
operator|.
name|readRoles
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|writeACLs
operator|!=
literal|null
condition|?
operator|!
name|writeACLs
operator|.
name|equals
argument_list|(
name|that
operator|.
name|writeACLs
argument_list|)
else|:
name|that
operator|.
name|writeACLs
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|writeRoles
operator|!=
literal|null
condition|?
operator|!
name|writeRoles
operator|.
name|equals
argument_list|(
name|that
operator|.
name|writeRoles
argument_list|)
else|:
name|that
operator|.
name|writeRoles
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|readACLs
operator|!=
literal|null
condition|?
name|readACLs
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|writeACLs
operator|!=
literal|null
condition|?
name|writeACLs
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|adminACLs
operator|!=
literal|null
condition|?
name|adminACLs
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|adminRoles
operator|!=
literal|null
condition|?
name|adminRoles
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|readRoles
operator|!=
literal|null
condition|?
name|readRoles
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|writeRoles
operator|!=
literal|null
condition|?
name|writeRoles
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|groupClass
operator|!=
literal|null
condition|?
name|groupClass
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

