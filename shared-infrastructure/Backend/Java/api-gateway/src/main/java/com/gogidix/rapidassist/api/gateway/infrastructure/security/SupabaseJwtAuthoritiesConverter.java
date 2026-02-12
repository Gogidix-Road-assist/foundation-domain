package com.gogidix.rapidassist.api.gateway.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SupabaseJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final SupabaseSecurityProperties props;

    public SupabaseJwtAuthoritiesConverter(SupabaseSecurityProperties props) {
        this.props = props;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.addAll(readStringOrListClaim(jwt, props.getRolesClaim()).stream()
                .filter(StringUtils::hasText)
                .map(this::toRoleAuthority)
                .toList());

        authorities.addAll(readStringOrListClaim(jwt, props.getPermissionsClaim()).stream()
                .filter(StringUtils::hasText)
                .map(this::toPermissionAuthority)
                .toList());

        return dedupe(authorities);
    }

    private GrantedAuthority toRoleAuthority(String role) {
        String name = role.startsWith(props.getRolePrefix()) ? role : props.getRolePrefix() + role;
        return new SimpleGrantedAuthority(name);
    }

    private GrantedAuthority toPermissionAuthority(String permission) {
        String name = permission.startsWith(props.getPermissionPrefix()) ? permission : props.getPermissionPrefix() + permission;
        return new SimpleGrantedAuthority(name);
    }

    private static List<String> readStringOrListClaim(Jwt jwt, String claimName) {
        if (!StringUtils.hasText(claimName)) {
            return List.of();
        }

        Object claim = jwt.getClaims().get(claimName);
        if (claim == null) {
            return List.of();
        }

        if (claim instanceof String s) {
            return splitOnCommonDelimiters(s);
        }

        if (claim instanceof Collection<?> c) {
            return c.stream().map(String::valueOf).toList();
        }

        return List.of(String.valueOf(claim));
    }

    private static List<String> splitOnCommonDelimiters(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return List.of(value.split("[ ,]"))
                .stream()
                .filter(StringUtils::hasText)
                .toList();
    }

    private static List<GrantedAuthority> dedupe(List<GrantedAuthority> input) {
        Set<String> seen = input.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return seen.stream().map(a -> (GrantedAuthority) new SimpleGrantedAuthority(a)).toList();
    }
}
