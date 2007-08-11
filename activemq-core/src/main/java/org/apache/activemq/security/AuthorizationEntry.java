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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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

begin_comment
comment|/**  * Represents an entry in a {@link DefaultAuthorizationMap} for assigning  * different operations (read, write, admin) of user roles to a specific  * destination or a hierarchical wildcard area of destinations.  *   * @org.apache.xbean.XBean  * @version $Revision$  */
end_comment

begin_class
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
specifier|private
name|String
name|adminRoles
decl_stmt|;
specifier|private
name|String
name|readRoles
decl_stmt|;
specifier|private
name|String
name|writeRoles
decl_stmt|;
specifier|private
name|String
name|groupClass
init|=
literal|"org.apache.activemq.jaas.GroupPrincipal"
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
name|Class
index|[]
name|paramClass
init|=
operator|new
name|Class
index|[
literal|1
index|]
decl_stmt|;
name|paramClass
index|[
literal|0
index|]
operator|=
name|String
operator|.
name|class
expr_stmt|;
name|Object
index|[]
name|param
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
name|param
index|[
literal|0
index|]
operator|=
name|name
expr_stmt|;
try|try
block|{
name|Class
name|cls
init|=
name|Class
operator|.
name|forName
argument_list|(
name|groupClass
argument_list|)
decl_stmt|;
name|Constructor
index|[]
name|constructors
init|=
name|cls
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|constructors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Class
index|[]
name|paramTypes
init|=
name|constructors
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|!=
literal|0
operator|&&
name|paramTypes
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|paramClass
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|<
name|constructors
operator|.
name|length
condition|)
block|{
name|Object
name|instance
init|=
name|constructors
index|[
name|i
index|]
operator|.
name|newInstance
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Object
name|instance
init|=
name|cls
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Method
index|[]
name|methods
init|=
name|cls
operator|.
name|getMethods
argument_list|()
decl_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Class
index|[]
name|paramTypes
init|=
name|methods
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|!=
literal|0
operator|&&
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"setName"
argument_list|)
operator|&&
name|paramTypes
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|paramClass
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|<
name|methods
operator|.
name|length
condition|)
block|{
name|methods
index|[
name|i
index|]
operator|.
name|invoke
argument_list|(
name|instance
argument_list|,
name|param
argument_list|)
expr_stmt|;
name|answer
operator|.
name|add
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchMethodException
argument_list|()
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
if|if
condition|(
name|adminRoles
operator|!=
literal|null
condition|)
block|{
name|setAdminACLs
argument_list|(
name|parseACLs
argument_list|(
name|adminRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeRoles
operator|!=
literal|null
condition|)
block|{
name|setWriteACLs
argument_list|(
name|parseACLs
argument_list|(
name|writeRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|readRoles
operator|!=
literal|null
condition|)
block|{
name|setReadACLs
argument_list|(
name|parseACLs
argument_list|(
name|readRoles
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

