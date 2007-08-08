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
package|;
end_package

begin_comment
comment|/** * A holder for different thread priorites used in ActiveMQ *  * @version $Revision: 1.9 $ */
end_comment

begin_interface
specifier|public
interface|interface
name|ThreadPriorities
block|{
specifier|public
specifier|static
specifier|final
name|int
name|INBOUND_BROKER_CONNECTION
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OUT_BOUND_BROKER_DISPATCH
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|INBOUND_CLIENT_CONNECTION
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|INBOUND_CLIENT_SESSION
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|BROKER_MANAGEMENT
init|=
literal|9
decl_stmt|;
block|}
end_interface

end_unit

