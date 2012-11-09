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
name|broker
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|JobSupport
block|{
specifier|public
specifier|static
name|String
name|getDateTime
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
operator|new
name|Date
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|dateFormat
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|long
name|getDataTime
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|DateFormat
name|dfm
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
name|dfm
operator|.
name|parse
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|date
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit
