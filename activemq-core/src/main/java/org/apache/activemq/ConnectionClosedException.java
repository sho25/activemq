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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
import|;
end_import

begin_comment
comment|/**  * An exception thrown when attempt is made to use a connection when the connection has been closed.  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionClosedException
extends|extends
name|IllegalStateException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7681404582227153308L
decl_stmt|;
specifier|public
name|ConnectionClosedException
parameter_list|()
block|{
name|super
argument_list|(
literal|"The connection is already closed"
argument_list|,
literal|"AlreadyClosed"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

