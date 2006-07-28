begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Used to start and stop transports as well as terminating clients.  *   * @openwire:marshaller code="18"  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionControl
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
name|CONNECTION_CONTROL
decl_stmt|;
specifier|protected
name|boolean
name|suspend
decl_stmt|;
specifier|protected
name|boolean
name|resume
decl_stmt|;
specifier|protected
name|boolean
name|close
decl_stmt|;
specifier|protected
name|boolean
name|exit
decl_stmt|;
specifier|protected
name|boolean
name|faultTolerant
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
literal|null
return|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the close.      */
specifier|public
name|boolean
name|isClose
parameter_list|()
block|{
return|return
name|close
return|;
block|}
comment|/**      * @param close      *            The close to set.      */
specifier|public
name|void
name|setClose
parameter_list|(
name|boolean
name|close
parameter_list|)
block|{
name|this
operator|.
name|close
operator|=
name|close
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the exit.      */
specifier|public
name|boolean
name|isExit
parameter_list|()
block|{
return|return
name|exit
return|;
block|}
comment|/**      * @param exit      *            The exit to set.      */
specifier|public
name|void
name|setExit
parameter_list|(
name|boolean
name|exit
parameter_list|)
block|{
name|this
operator|.
name|exit
operator|=
name|exit
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the faultTolerant.      */
specifier|public
name|boolean
name|isFaultTolerant
parameter_list|()
block|{
return|return
name|faultTolerant
return|;
block|}
comment|/**      * @param faultTolerant      *            The faultTolerant to set.      */
specifier|public
name|void
name|setFaultTolerant
parameter_list|(
name|boolean
name|faultTolerant
parameter_list|)
block|{
name|this
operator|.
name|faultTolerant
operator|=
name|faultTolerant
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the resume.      */
specifier|public
name|boolean
name|isResume
parameter_list|()
block|{
return|return
name|resume
return|;
block|}
comment|/**      * @param resume      *            The resume to set.      */
specifier|public
name|void
name|setResume
parameter_list|(
name|boolean
name|resume
parameter_list|)
block|{
name|this
operator|.
name|resume
operator|=
name|resume
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      * @return Returns the suspend.      */
specifier|public
name|boolean
name|isSuspend
parameter_list|()
block|{
return|return
name|suspend
return|;
block|}
comment|/**      * @param suspend      *            The suspend to set.      */
specifier|public
name|void
name|setSuspend
parameter_list|(
name|boolean
name|suspend
parameter_list|)
block|{
name|this
operator|.
name|suspend
operator|=
name|suspend
expr_stmt|;
block|}
block|}
end_class

end_unit

