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
name|transport
operator|.
name|stomp
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_comment
comment|/**  * Basic interface that mediates between protocol converter and transport  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|StompTransport
block|{
specifier|public
name|void
name|sendToActiveMQ
parameter_list|(
name|Command
name|command
parameter_list|)
function_decl|;
specifier|public
name|void
name|sendToStomp
parameter_list|(
name|StompFrame
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
function_decl|;
specifier|public
name|StompInactivityMonitor
name|getInactivityMonitor
parameter_list|()
function_decl|;
specifier|public
name|StompWireFormat
name|getWireFormat
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

