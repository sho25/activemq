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
comment|/**  * Represents the end marker of a stream of {@link PartialCommand} instances.  *   * @openwire:marshaller code="61"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LastPartialCommand
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
name|PARTIAL_LAST_COMMAND
decl_stmt|;
specifier|public
name|LastPartialCommand
parameter_list|()
block|{     }
specifier|public
name|LastPartialCommand
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{
name|setResponseRequired
argument_list|(
name|responseRequired
argument_list|)
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The transport layer should filter out LastPartialCommand instances but received: "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|/**      * Lets copy across the required fields from this last partial command to      * the newly unmarshalled complete command      *      * @param completeCommand the newly unmarshalled complete command      */
specifier|public
name|void
name|configure
parameter_list|(
name|Command
name|completeCommand
parameter_list|)
block|{
comment|// copy across the transient properties
name|completeCommand
operator|.
name|setFrom
argument_list|(
name|getFrom
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO should not be required as the large command would be marshalled with this property
comment|//completeCommand.setResponseRequired(isResponseRequired());
block|}
block|}
end_class

end_unit

