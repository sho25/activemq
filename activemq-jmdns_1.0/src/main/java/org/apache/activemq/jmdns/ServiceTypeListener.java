begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2003-2005 Arthur van Hoff, Rick Blair  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_comment
comment|/**  * Listener for service types.  *  * @version %I%, %G%  * @author	Arthur van Hoff, Werner Randelshofer  */
end_comment

begin_interface
specifier|public
interface|interface
name|ServiceTypeListener
extends|extends
name|EventListener
block|{
comment|/**      * A new service type was discovered.      *      * @param event The service event providing the fully qualified type of      *              the service.      */
name|void
name|serviceTypeAdded
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

