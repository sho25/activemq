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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jaas
operator|.
name|GroupPrincipal
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
comment|/**  * Represents an entry in a {@link DefaultAuthorizationMap} for assigning  * different operations (read, write, admin) of user roles to a specific  * destination or a hierarchical wildcard area of destinations.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
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
name|readACLs
init|=
name|Collections
operator|.
name|EMPTY_SET
decl_stmt|;
specifier|private
name|Set
name|writeACLs
init|=
name|Collections
operator|.
name|EMPTY_SET
decl_stmt|;
specifier|private
name|Set
name|adminACLs
init|=
name|Collections
operator|.
name|EMPTY_SET
decl_stmt|;
specifier|public
name|Set
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
comment|// -------------------------------------------------------------------------
specifier|public
name|void
name|setAdmin
parameter_list|(
name|String
name|roles
parameter_list|)
block|{
name|setAdminACLs
argument_list|(
name|parseACLs
argument_list|(
name|roles
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
block|{
name|setReadACLs
argument_list|(
name|parseACLs
argument_list|(
name|roles
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
block|{
name|setWriteACLs
argument_list|(
name|parseACLs
argument_list|(
name|roles
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Set
name|parseACLs
parameter_list|(
name|String
name|roles
parameter_list|)
block|{
name|Set
name|answer
init|=
operator|new
name|HashSet
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
name|answer
operator|.
name|add
argument_list|(
operator|new
name|GroupPrincipal
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

