begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|oneport
operator|.
name|openorb
package|;
end_package

begin_comment
comment|/**  * Interface definition: TestIIOPServer.  *   * @author OpenORB Compiler  */
end_comment

begin_class
specifier|public
class|class
name|TestIIOPServerPOATie
extends|extends
name|TestIIOPServerPOA
block|{
comment|//
comment|// Private reference to implementation object
comment|//
specifier|private
name|TestIIOPServerOperations
name|_tie
decl_stmt|;
comment|//
comment|// Private reference to POA
comment|//
specifier|private
name|org
operator|.
name|omg
operator|.
name|PortableServer
operator|.
name|POA
name|_poa
decl_stmt|;
comment|/**      * Constructor      */
specifier|public
name|TestIIOPServerPOATie
parameter_list|(
name|TestIIOPServerOperations
name|tieObject
parameter_list|)
block|{
name|_tie
operator|=
name|tieObject
expr_stmt|;
block|}
comment|/**      * Constructor      */
specifier|public
name|TestIIOPServerPOATie
parameter_list|(
name|TestIIOPServerOperations
name|tieObject
parameter_list|,
name|org
operator|.
name|omg
operator|.
name|PortableServer
operator|.
name|POA
name|poa
parameter_list|)
block|{
name|_tie
operator|=
name|tieObject
expr_stmt|;
name|_poa
operator|=
name|poa
expr_stmt|;
block|}
comment|/**      * Get the delegate      */
specifier|public
name|TestIIOPServerOperations
name|_delegate
parameter_list|()
block|{
return|return
name|_tie
return|;
block|}
comment|/**      * Set the delegate      */
specifier|public
name|void
name|_delegate
parameter_list|(
name|TestIIOPServerOperations
name|delegate_
parameter_list|)
block|{
name|_tie
operator|=
name|delegate_
expr_stmt|;
block|}
comment|/**      * _default_POA method      */
specifier|public
name|org
operator|.
name|omg
operator|.
name|PortableServer
operator|.
name|POA
name|_default_POA
parameter_list|()
block|{
if|if
condition|(
name|_poa
operator|!=
literal|null
condition|)
return|return
name|_poa
return|;
else|else
return|return
name|super
operator|.
name|_default_POA
argument_list|()
return|;
block|}
comment|/**      * Operation test      */
specifier|public
name|void
name|test
parameter_list|()
block|{
name|_tie
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

