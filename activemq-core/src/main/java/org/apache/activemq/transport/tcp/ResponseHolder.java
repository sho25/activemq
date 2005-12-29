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
name|tcp
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
name|command
operator|.
name|Response
import|;
end_import

begin_comment
comment|/**  * ResponseHolder utility  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ResponseHolder
block|{
specifier|protected
name|Response
name|response
decl_stmt|;
specifier|protected
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|notified
decl_stmt|;
comment|/**      * Construct a receipt holder      */
specifier|public
name|ResponseHolder
parameter_list|()
block|{     }
comment|/**      * Set the Response for this holder      *      * @param r      */
specifier|public
name|void
name|setResponse
parameter_list|(
name|Response
name|r
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|this
operator|.
name|response
operator|=
name|r
expr_stmt|;
name|notified
operator|=
literal|true
expr_stmt|;
name|lock
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Get the Response      *       * @return the Response or null if it is closed      */
specifier|public
name|Response
name|getResponse
parameter_list|()
block|{
return|return
name|getResponse
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**      * wait upto<Code>timeout</Code> timeout ms to get a receipt      *      * @param timeout      * @return      */
specifier|public
name|Response
name|getResponse
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
operator|!
name|notified
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|this
operator|.
name|response
return|;
block|}
comment|/**      * close this holder      */
specifier|public
name|void
name|close
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|notified
operator|=
literal|true
expr_stmt|;
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

