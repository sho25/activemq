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
comment|/**  * Represents a destination based configuration of policies so that individual  * destinations or wildcard hierarchies of destinations can be configured using  * different policies. Each entry in the map represents the authorization ACLs  * for each operation.  *  * @org.apache.xbean.XBean element="authorizationMap"  *  */
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
specifier|private
name|String
name|groupClass
init|=
literal|"org.apache.activemq.jaas.GroupPrincipal"
decl_stmt|;
specifier|public
name|DefaultAuthorizationMap
parameter_list|()
block|{     }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|DefaultAuthorizationMap
parameter_list|(
name|List
argument_list|<
name|DestinationMapEntry
argument_list|>
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
argument_list|<
name|Object
argument_list|>
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
argument_list|<
name|Object
argument_list|>
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
argument_list|<
name|Object
argument_list|>
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
argument_list|<
name|Object
argument_list|>
name|getAdminACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
argument_list|<
name|AuthorizationEntry
argument_list|>
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
argument_list|<
name|AuthorizationEntry
argument_list|>
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
argument_list|<
name|Object
argument_list|>
name|getReadACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
argument_list|<
name|AuthorizationEntry
argument_list|>
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
argument_list|<
name|AuthorizationEntry
argument_list|>
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
argument_list|<
name|Object
argument_list|>
name|getWriteACLs
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
argument_list|<
name|AuthorizationEntry
argument_list|>
name|entries
init|=
name|getAllEntries
argument_list|(
name|destination
argument_list|)
decl_stmt|;
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
comment|// now lets go through each entry adding individual
for|for
control|(
name|Iterator
argument_list|<
name|AuthorizationEntry
argument_list|>
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
comment|/**      * Looks up the value(s) matching the given Destination key. For simple      * destinations this is typically a List of one single value, for wildcards      * or composite destinations this will typically be a Union of matching      * values.      *      * @param key the destination to lookup      * @return a Union of matching values or an empty list if there are no      *         matching values.      */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|public
specifier|synchronized
name|Set
name|get
parameter_list|(
name|ActiveMQDestination
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|key
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
name|Set
name|answer
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|childDestination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|answer
operator|=
name|union
argument_list|(
name|answer
argument_list|,
name|get
argument_list|(
name|childDestination
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
operator|||
name|answer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
return|return
name|answer
return|;
block|}
return|return
name|findWildcardMatches
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Sets the individual entries on the authorization map      *      * @org.apache.xbean.ElementType class="org.apache.activemq.security.AuthorizationEntry"      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|setAuthorizationEntries
parameter_list|(
name|List
argument_list|<
name|DestinationMapEntry
argument_list|>
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|DestinationMapEntry
argument_list|>
name|getEntryClass
parameter_list|()
block|{
return|return
name|AuthorizationEntry
operator|.
name|class
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|Set
argument_list|<
name|AuthorizationEntry
argument_list|>
name|getAllEntries
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
argument_list|<
name|AuthorizationEntry
argument_list|>
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
specifier|public
name|String
name|getGroupClass
parameter_list|()
block|{
return|return
name|groupClass
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
specifier|static
name|Object
name|createGroupPrincipal
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|groupClass
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
index|[]
name|param
init|=
operator|new
name|Object
index|[]
block|{
name|name
block|}
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
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
argument_list|<
name|?
argument_list|>
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
name|Object
name|instance
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
argument_list|<
name|?
argument_list|>
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
name|String
operator|.
name|class
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
name|instance
operator|=
name|constructors
index|[
name|i
index|]
operator|.
name|newInstance
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instance
operator|=
name|cls
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
argument_list|<
name|?
argument_list|>
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
name|String
operator|.
name|class
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
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

