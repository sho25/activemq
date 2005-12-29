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
name|activecluster
operator|.
name|group
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|Node
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

begin_comment
comment|/**  * A filter configured with a list of DMZ zones on which to restrict which nodes  * are allowed to be master nodes.  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|MasterZoneFilter
implements|implements
name|NodeFilter
block|{
specifier|private
name|List
name|zones
decl_stmt|;
specifier|public
name|MasterZoneFilter
parameter_list|(
name|List
name|zones
parameter_list|)
block|{
name|this
operator|.
name|zones
operator|=
name|zones
expr_stmt|;
block|}
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|Object
name|zone
init|=
name|node
operator|.
name|getZone
argument_list|()
decl_stmt|;
return|return
name|zones
operator|.
name|contains
argument_list|(
name|zone
argument_list|)
return|;
block|}
block|}
end_class

end_unit

