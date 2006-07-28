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
name|activeio
operator|.
name|xnet
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
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $ $Date: 2004/04/09 19:04:01 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|SocketService
block|{
name|void
name|service
parameter_list|(
name|Socket
name|socket
parameter_list|)
throws|throws
name|ServiceException
throws|,
name|IOException
function_decl|;
comment|/**      * Gets the name of the service.      * Used for display purposes only      */
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

