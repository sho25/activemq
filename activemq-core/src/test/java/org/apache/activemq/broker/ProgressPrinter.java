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
name|activemq
operator|.
name|broker
package|;
end_package

begin_class
specifier|public
class|class
name|ProgressPrinter
block|{
specifier|private
specifier|final
name|long
name|total
decl_stmt|;
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
name|long
name|percentDone
init|=
literal|0
decl_stmt|;
name|long
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|ProgressPrinter
parameter_list|(
name|long
name|total
parameter_list|,
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|increment
parameter_list|()
block|{
name|update
argument_list|(
operator|++
name|counter
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|update
parameter_list|(
name|long
name|current
parameter_list|)
block|{
name|long
name|at
init|=
literal|100
operator|*
name|current
operator|/
name|total
decl_stmt|;
if|if
condition|(
operator|(
name|percentDone
operator|/
name|interval
operator|)
operator|!=
operator|(
name|at
operator|/
name|interval
operator|)
condition|)
block|{
name|percentDone
operator|=
name|at
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Completed: "
operator|+
name|percentDone
operator|+
literal|"%"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

