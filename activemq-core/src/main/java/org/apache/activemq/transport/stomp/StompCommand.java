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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_interface
interface|interface
name|StompCommand
block|{
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|commandLine
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
function_decl|;
comment|/**      * Returns a command instance which always returns null for a packet       */
name|StompCommand
name|NULL_COMMAND
init|=
operator|new
name|StompCommand
argument_list|()
block|{
specifier|public
name|CommandEnvelope
name|build
parameter_list|(
name|String
name|commandLine
parameter_list|,
name|DataInput
name|in
parameter_list|)
block|{
return|return
operator|new
name|CommandEnvelope
argument_list|(
literal|null
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

