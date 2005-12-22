begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|xnet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * The Server will call the following methods.  *<p/>  * newInstance()  * init( port, properties)  * start()  * stop()  *<p/>  * All ServerService implementations must have a no argument  * constructor.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ServerService
extends|extends
name|SocketService
block|{
specifier|public
name|void
name|init
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|ServiceException
function_decl|;
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|ServiceException
function_decl|;
comment|/**      * Gets the ip number that the      * daemon is listening on.      */
specifier|public
name|String
name|getIP
parameter_list|()
function_decl|;
comment|/**      * Gets the port number that the      * daemon is listening on.      */
specifier|public
name|int
name|getPort
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

