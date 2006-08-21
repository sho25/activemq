begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|com
operator|.
name|panacya
operator|.
name|platform
operator|.
name|service
operator|.
name|bus
operator|.
name|sender
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|EJBObject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Sender
extends|extends
name|EJBObject
block|{
specifier|public
name|void
name|sendMessage
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|RemoteException
throws|,
name|SenderException
function_decl|;
block|}
end_interface

end_unit

