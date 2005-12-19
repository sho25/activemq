begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|oneport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_class
class|class
name|UnknownRecognizer
implements|implements
name|ProtocolRecognizer
block|{
specifier|static
specifier|public
specifier|final
name|ProtocolRecognizer
name|UNKNOWN_RECOGNIZER
init|=
operator|new
name|UnknownRecognizer
argument_list|()
decl_stmt|;
specifier|private
name|UnknownRecognizer
parameter_list|()
block|{             }
specifier|public
name|boolean
name|recognizes
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
if|if
condition|(
name|packet
operator|.
name|limit
argument_list|()
operator|>
literal|15
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

