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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_comment
comment|/**  * Interface for classes that will be called by the TransportLogger  * class to actually write to a log file.  * Every class that implements this interface has do be declared in  * the resources/META-INF/services/org/apache/activemq/transport/logwriters  * directory, by creating a file with the name of the writer (for example  * "default") and including the line  * class=org.apache.activemq.transport.logwriters.(Name of the LogWriter class)  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|LogWriter
block|{
comment|/**      * Writes a header message to the log.      * @param log The log to be written to.      */
specifier|public
name|void
name|initialMessage
parameter_list|(
name|Log
name|log
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when a request command is sent.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logRequest
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when a response command is received.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logResponse
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|response
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when an asynchronous equest command is sent.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logAsyncRequest
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when message is sent.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logOneWay
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when message is received.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logReceivedCommand
parameter_list|(
name|Log
name|log
parameter_list|,
name|Object
name|command
parameter_list|)
function_decl|;
comment|/**      * Writes a message to a log when an exception is received.      * @param log The log to be written to.      * @param command The command to be logged.      */
specifier|public
name|void
name|logReceivedException
parameter_list|(
name|Log
name|log
parameter_list|,
name|IOException
name|error
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

