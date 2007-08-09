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
name|ActiveMQDestination
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
name|DestinationMap
import|;
end_import

begin_comment
comment|/**  * Represents a destination based configuration of policies so that individual  * destinations or wildcard hierarchies of destinations can be configured using  * different policies. Each entry in the map represents the authorization ACLs  * for each operation.  *   * @org.apache.xbean.XBean element="authorizationMap"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DefaultAuthorizationMap
extends|extends
name|DestinationMap
implements|implements
name|AuthorizationMap
block|{
specifier|private
name|AuthorizationEntry
name|defaultEntry
decl_stmt|;
specifier|private
name|TempDestinationAuthorizationEntry
name|tempDestinationAuthorizationEntry
decl_stmt|;
specifier|public
name|DefaultAuthorizationMap
parameter_list|()
block|{     }
specifier|public
name|DefaultAuthorizationMap
parameter_list|(
name|List
name|authorizationEntries
parameter_list|)
block|{
name|setAuthorizationEntries
argument_list|(
name|authorizationEntries
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTempDestinationAuthorizationEntry
parameter_list|(
name|TempDestinationAuthorizationEntry
name|tempDestinationAuthorizationEntry
parameter_list|)
block|{
name|this
operator|.
name|tempDestinationAuthorizationEntry
operator|=
name|tempDestinationAuthorizationEntry
expr_stmt|;
block|}
specifier|public
name|TempDestinationAuthorizationEntry
name|getTempDestinationAuthorizationEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|tempDestinationAuthorizationEntry
return|;
block|}
specifier|public
name|Set
name|getTempDestinationAdminACLs
parameter_list|()
block|{
if|if
condition|(
name|tempDestinationAuthorizationEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|tempDestinationAuthorizationEntry
operator|.
name|getAdminACLs
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|Set
name|getTempDestinationReadACLs
parameter_list|()
block|{
if|if
condition|(
name|tempDestinationAuthorizationEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|tempDestinationAuthorizationEntry
operator|.
name|getReadACLs
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|Set
name|getTempDestinationWriteACLs
parameter_list|()
block|{
if|if
condition|(
name|tempDestinationAuthorizationEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|tempDestinationAuthorizationEntry
operator|.
name|getWriteACLs
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|Set
name|getAdminACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
name|iter
init|=
name|entries
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AuthorizationEntry
name|entry
init|=
operator|(
name|AuthorizationEntry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|answer
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getAdminACLs
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|Set
name|getReadACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
name|iter
init|=
name|entries
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AuthorizationEntry
name|entry
init|=
operator|(
name|AuthorizationEntry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|answer
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getReadACLs
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|Set
name|getWriteACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Set
name|answer
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
name|iter
init|=
name|entries
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AuthorizationEntry
name|entry
init|=
operator|(
name|AuthorizationEntry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|answer
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getWriteACLs
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|AuthorizationEntry
name|getEntryFor
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|AuthorizationEntry
name|answer
init|=
operator|(
name|AuthorizationEntry
operator|)
name|chooseValue
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
name|getDefaultEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/**      * Sets the individual entries on the authorization map      *       * @org.apache.xbean.ElementType class="org.apache.activemq.security.AuthorizationEntry"      */
specifier|public
name|void
name|setAuthorizationEntries
parameter_list|(
name|List
name|entries
parameter_list|)
block|{
name|super
operator|.
name|setEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AuthorizationEntry
name|getDefaultEntry
parameter_list|()
block|{
return|return
name|defaultEntry
return|;
block|}
specifier|public
name|void
name|setDefaultEntry
parameter_list|(
name|AuthorizationEntry
name|defaultEntry
parameter_list|)
block|{
name|this
operator|.
name|defaultEntry
operator|=
name|defaultEntry
expr_stmt|;
block|}
specifier|protected
name|Class
name|getEntryClass
parameter_list|()
block|{
return|return
name|AuthorizationEntry
operator|.
name|class
return|;
block|}
specifier|protected
name|Set
name|getAllEntries
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|entries
init|=
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultEntry
operator|!=
literal|null
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|defaultEntry
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
block|}
end_class

end_unit

