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
name|command
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * Used to start and stop transports as well as terminating clients.  *   * @openwire:marshaller code="14"  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ControlCommand
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|CONTROL_COMMAND
decl_stmt|;
specifier|private
name|String
name|command
decl_stmt|;
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
name|String
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
specifier|public
name|void
name|setCommand
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|visitor
operator|.
name|processControlCommand
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

