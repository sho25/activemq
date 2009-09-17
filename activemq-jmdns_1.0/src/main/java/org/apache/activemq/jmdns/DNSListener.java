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

begin_comment
comment|// REMIND: Listener should follow Java idiom for listener or have a different
end_comment

begin_comment
comment|//         name.
end_comment

begin_comment
comment|/**  * DNSListener.  * Listener for record updates.  *  * @author Werner Randelshofer, Rick Blair  * @version 1.0  May 22, 2004  Created.  */
end_comment

begin_interface
interface|interface
name|DNSListener
block|{
comment|/**      * Update a DNS record.      */
name|void
name|updateRecord
parameter_list|(
name|JmDNS
name|jmdns
parameter_list|,
name|long
name|now
parameter_list|,
name|DNSRecord
name|record
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

