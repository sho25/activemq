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
name|memory
operator|.
name|buffer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_comment
comment|/**  * A message implementation which is useful for testing as we can spoof its size  *    * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DummyMessage
extends|extends
name|ActiveMQMessage
block|{
specifier|private
name|int
name|size
decl_stmt|;
specifier|public
name|DummyMessage
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DummyMessage[id="
operator|+
name|getMessageId
argument_list|()
operator|+
literal|" size="
operator|+
name|size
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

