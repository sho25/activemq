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
name|broker
operator|.
name|console
operator|.
name|filter
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
specifier|public
class|class
name|GroupPropertiesViewFilter
extends|extends
name|PropertiesViewFilter
block|{
comment|/**      * Creates a group properties filter that is able to filter the display result based on a group prefix      * @param next - the next query filter      */
specifier|public
name|GroupPropertiesViewFilter
parameter_list|(
name|QueryFilter
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a group properties filter that is able to filter the display result based on a group prefix      * @param groupView - the group filter to use      * @param next - the next query filter      */
specifier|public
name|GroupPropertiesViewFilter
parameter_list|(
name|Set
name|groupView
parameter_list|,
name|QueryFilter
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|groupView
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
comment|/**      * Filter the properties that matches the group prefix only.      * @param data - map data to filter      * @return - filtered map data      */
specifier|protected
name|Map
name|filterView
parameter_list|(
name|Map
name|data
parameter_list|)
block|{
comment|// If no view specified, display all attributes
if|if
condition|(
name|viewFilter
operator|==
literal|null
operator|||
name|viewFilter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|data
return|;
block|}
name|Map
name|newData
decl_stmt|;
try|try
block|{
comment|// Lets try to use the same class as the original
name|newData
operator|=
operator|(
name|Map
operator|)
name|data
operator|.
name|getClass
argument_list|()
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Lets use a default HashMap
name|newData
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
comment|// Filter the keys to view
for|for
control|(
name|Iterator
name|i
init|=
name|data
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// Checks if key matches any of the group filter
for|for
control|(
name|Iterator
name|j
init|=
name|viewFilter
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|group
init|=
operator|(
name|String
operator|)
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|newData
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|newData
return|;
block|}
block|}
end_class

end_unit

