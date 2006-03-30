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
name|transport
operator|.
name|stomp
package|;
end_package

begin_class
class|class
name|AsyncHelper
block|{
specifier|public
specifier|static
name|Object
name|tryUntilNotInterrupted
parameter_list|(
name|HelperWithReturn
name|helper
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|helper
operator|.
name|cycle
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|void
name|tryUntilNotInterrupted
parameter_list|(
specifier|final
name|Helper
name|helper
parameter_list|)
block|{
name|tryUntilNotInterrupted
argument_list|(
operator|new
name|HelperWithReturn
argument_list|()
block|{
specifier|public
name|Object
name|cycle
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|helper
operator|.
name|cycle
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
interface|interface
name|HelperWithReturn
block|{
name|Object
name|cycle
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
block|}
interface|interface
name|Helper
block|{
name|void
name|cycle
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
block|}
block|}
end_class

end_unit

