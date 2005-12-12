begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|command
package|;
end_package

begin_comment
comment|/**  *   * @openwire:marshaller  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DataResponse
extends|extends
name|Response
block|{
name|DataStructure
name|data
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|DATA_RESPONSE
decl_stmt|;
specifier|public
name|DataResponse
parameter_list|()
block|{         }
specifier|public
name|DataResponse
parameter_list|(
name|DataStructure
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|DataStructure
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|void
name|setData
parameter_list|(
name|DataStructure
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
block|}
end_class

end_unit

