package common.management.common.model;

import common.management.common.util.GenericListJsonConvertor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "roles")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity{
	@Column(length = 128 ,unique = true,nullable = false)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "roles_privileges",
			joinColumns = @JoinColumn(
					name = "role_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
					name = "privilege_id", referencedColumnName = "id"))
	private Collection<Privilege> privileges = new ArrayList<>();

	@Convert(converter = GenericListJsonConvertor.class)
	private List<String> permissions = new ArrayList<>();
}