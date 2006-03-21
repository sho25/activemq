begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|MapContainer
import|;
end_import

begin_comment
comment|/** * Map.Entry implementation for a container *  * @version $Revision: 1.2 $ */
end_comment

begin_class
class|class
name|ContainerMapEntry
implements|implements
name|Map
operator|.
name|Entry
block|{
specifier|private
name|MapContainer
name|container
decl_stmt|;
specifier|private
name|Object
name|key
decl_stmt|;
name|ContainerMapEntry
parameter_list|(
name|MapContainer
name|container
parameter_list|,
name|Object
name|key
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
name|container
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|Object
name|setValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|container
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

