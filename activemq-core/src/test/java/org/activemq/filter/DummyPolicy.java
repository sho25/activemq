begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
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
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a destination based policy  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DummyPolicy
extends|extends
name|DestinationMap
block|{
specifier|protected
name|Class
name|getEntryClass
parameter_list|()
block|{
return|return
name|DummyPolicyEntry
operator|.
name|class
return|;
block|}
specifier|public
name|void
name|setEntries
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
block|}
end_class

end_unit

