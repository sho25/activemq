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
package|;
end_package

begin_comment
comment|/**  * Inserts itself into the BrokerStack  *   * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|InsertableMutableBrokerFilter
extends|extends
name|MutableBrokerFilter
block|{
name|MutableBrokerFilter
name|parent
decl_stmt|;
specifier|public
name|InsertableMutableBrokerFilter
parameter_list|(
name|MutableBrokerFilter
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|parent
operator|.
name|setNext
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove 'self' from the BrokerStack      */
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|parent
operator|.
name|setNext
argument_list|(
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

