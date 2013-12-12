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
name|shiro
operator|.
name|authz
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|authz
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|shiro
operator|.
name|authz
operator|.
name|permission
operator|.
name|WildcardPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @since 5.10.0  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQWildcardPermissionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testNotWildcardPermission
parameter_list|()
block|{
name|ActiveMQWildcardPermission
name|perm
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"topic:TEST:*"
argument_list|)
decl_stmt|;
name|Permission
name|dummy
init|=
operator|new
name|Permission
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|implies
parameter_list|(
name|Permission
name|p
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
name|assertFalse
argument_list|(
name|perm
operator|.
name|implies
argument_list|(
name|dummy
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIntrapartWildcard
parameter_list|()
block|{
name|ActiveMQWildcardPermission
name|superset
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"topic:ActiveMQ.Advisory.*:read"
argument_list|)
decl_stmt|;
name|ActiveMQWildcardPermission
name|subset
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"topic:ActiveMQ.Advisory.Topic:read"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|superset
operator|.
name|implies
argument_list|(
name|subset
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|subset
operator|.
name|implies
argument_list|(
name|superset
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatches
parameter_list|()
block|{
name|assertMatch
argument_list|(
literal|"x"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"xx"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"xy"
argument_list|,
literal|"xz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"?"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x?"
argument_list|,
literal|"xy"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"?y"
argument_list|,
literal|"xy"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x?z"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x*"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x*"
argument_list|,
literal|"xy"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"xy*"
argument_list|,
literal|"xy"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"xy*"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*x"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"*x"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*x"
argument_list|,
literal|"wx"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"*x"
argument_list|,
literal|"wz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*x"
argument_list|,
literal|"vwx"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x*z"
argument_list|,
literal|"xz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x*z"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x*z"
argument_list|,
literal|"xyyz"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"ab*t?z"
argument_list|,
literal|"abz"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"ab*d*yz"
argument_list|,
literal|"abcdz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"ab**cd**ef*yz"
argument_list|,
literal|"abcdefyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"a*c?*z"
argument_list|,
literal|"abcxyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"a*cd*z"
argument_list|,
literal|"abcdxyz"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*"
argument_list|,
literal|"x:x"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*"
argument_list|,
literal|"x:x:x"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x"
argument_list|,
literal|"x:y"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"x"
argument_list|,
literal|"x:y:z"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"foo?armat*"
argument_list|,
literal|"foobarmatches"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"f*"
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"foo"
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"fo*b"
argument_list|,
literal|"foob"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"fo*b*r"
argument_list|,
literal|"fooba"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"foo*"
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"t*k?ou"
argument_list|,
literal|"thankyou"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"he*l*world"
argument_list|,
literal|"helloworld"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"foo"
argument_list|,
literal|"foob"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory"
argument_list|,
literal|"foo:ActiveMQ.Advisory"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"*:ActiveMQ.Advisory"
argument_list|,
literal|"foo:ActiveMQ.Advisory."
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*"
argument_list|,
literal|"foo:ActiveMQ.Advisory"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*"
argument_list|,
literal|"foo:ActiveMQ.Advisory."
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory.*"
argument_list|,
literal|"foo:ActiveMQ.Advisory.Connection"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*:read"
argument_list|,
literal|"foo:ActiveMQ.Advisory.Connection:read"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*:read"
argument_list|,
literal|"foo:ActiveMQ.Advisory.Connection:write"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*:*"
argument_list|,
literal|"foo:ActiveMQ.Advisory.Connection:read"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"*:ActiveMQ.Advisory*:*"
argument_list|,
literal|"foo:ActiveMQ.Advisory."
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"topic"
argument_list|,
literal|"topic:TEST:*"
argument_list|)
expr_stmt|;
name|assertNoMatch
argument_list|(
literal|"*:ActiveMQ*"
argument_list|,
literal|"topic:TEST:*"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"topic:ActiveMQ.Advisory*"
argument_list|,
literal|"topic:ActiveMQ.Advisory.Connection:create"
argument_list|)
expr_stmt|;
name|assertMatch
argument_list|(
literal|"foo?ar"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|void
name|assertMatch
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|matches
argument_list|(
name|pattern
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|void
name|assertNoMatch
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|matches
argument_list|(
name|pattern
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|boolean
name|matches
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ActiveMQWildcardPermission
name|patternPerm
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|WildcardPermission
name|valuePerm
init|=
operator|new
name|WildcardPermission
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|patternPerm
operator|.
name|implies
argument_list|(
name|valuePerm
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPartsByReflectionThrowingException
parameter_list|()
block|{
name|ActiveMQWildcardPermission
name|perm
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"foo:bar"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|doGetPartsByReflection
parameter_list|(
name|WildcardPermission
name|wp
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Testing failure"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|WildcardPermission
name|otherPerm
init|=
operator|new
name|WildcardPermission
argument_list|(
literal|"foo:bar:baz"
argument_list|)
decl_stmt|;
name|perm
operator|.
name|implies
argument_list|(
name|otherPerm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImpliesWithExtraParts
parameter_list|()
block|{
name|ActiveMQWildcardPermission
name|perm1
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"foo:bar:baz"
argument_list|)
decl_stmt|;
name|ActiveMQWildcardPermission
name|perm2
init|=
operator|new
name|ActiveMQWildcardPermission
argument_list|(
literal|"foo:bar"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|perm1
operator|.
name|implies
argument_list|(
name|perm2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

